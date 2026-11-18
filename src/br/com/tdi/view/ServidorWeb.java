package br.com.tdi.view;

import br.com.tdi.dao.ProdutoDAO;
import br.com.tdi.model.Produto;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ServidorWeb {

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", new PaginaHandler());
            server.setExecutor(null);
            server.start();

            System.out.println("==================================================");
            System.out.println(">>> SERVIDOR WEB TDI INICIADO COM SUCESSO! <<<");
            System.out.println("Acesse no celular: http://192.168.1.130:8080");
            System.out.println("==================================================");

        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    static class PaginaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            ProdutoDAO dao = new ProdutoDAO();
            NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            // Processa cadastros, movimentações e exclusões vindos do celular
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder body = new StringBuilder();
                String linha;
                while ((linha = br.readLine()) != null) {
                    body.append(linha);
                }

                Map<String, String> params = parseForm(body.toString());
                String acao = params.get("acao");

                try {
                    if ("cadastrar".equals(acao)) {
                        String codPeca = params.get("codigo");
                        if (codPeca == null || codPeca.trim().isEmpty()) {
                            codPeca = "MOB-" + (System.currentTimeMillis() % 10000);
                        }
                        String descricao = params.get("descricao");
                        int qtd = Integer.parseInt(params.get("quantidade"));
                        int min = Integer.parseInt(params.get("minimo"));
                        String local = params.get("localizacao");
                        if (local == null || local.trim().isEmpty()) {
                            local = "Almoxarifado Central";
                        }
                        double custo = 0.0;
                        if (params.get("custo") != null && !params.get("custo").trim().isEmpty()) {
                            custo = Double.parseDouble(params.get("custo").replace(",", "."));
                        }

                        Produto p = new Produto();
                        p.setCodigoPeca(codPeca.trim());
                        p.setDescricao(descricao.trim());
                        p.setIdCategoria(1);
                        p.setQuantidadeEstoque(qtd);
                        p.setEstoqueMinimo(min);
                        p.setLocalizacaoPrateleira(local.trim());
                        p.setPrecoCusto(custo);

                        dao.salvar(p);

                    } else if ("excluir".equals(acao)) {
                        int id = Integer.parseInt(params.get("id"));
                        dao.excluir(id);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar acao no celular: " + e.getMessage());
                }

                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(303, -1);
                return;
            }

            // Calcula indicadores do banco de dados
            List<Produto> lista = null;
            int totalProdutos = 0;
            int totalUnidades = 0;
            int totalBaixo = 0;
            double valorPatrimonio = 0.0;
            String erroBanco = null;

            try {
                lista = dao.listarTodos();
                if (lista != null) {
                    totalProdutos = lista.size();
                    for (Produto p : lista) {
                        totalUnidades += p.getQuantidadeEstoque();
                        valorPatrimonio += (p.getQuantidadeEstoque() * p.getPrecoCusto());
                        if (p.getQuantidadeEstoque() <= p.getEstoqueMinimo()) {
                            totalBaixo++;
                        }
                    }
                }
            } catch (Exception ex) {
                erroBanco = ex.getMessage();
            }

            // Interface HTML/CSS modernizada no padrão TDI Máquinas Agrícolas
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'>");
            html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            html.append("<title>TDI Máquinas Agrícolas | Almoxarifado Mobile</title>");
            html.append("<script src='https://unpkg.com/html5-qrcode'></script>");
            html.append("<style>");
            html.append("* { box-sizing: border-box; font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, Roboto, sans-serif; }");
            html.append("body { margin: 0; background: #EEF4F9; color: #233445; padding-bottom: 30px; }");

            // Cabeçalho igual ao da interface Desktop
            html.append(".header { background: #193952; color: white; padding: 20px 16px; text-align: center; box-shadow: 0 2px 8px rgba(0,0,0,0.15); }");
            html.append(".header h1 { margin: 0; font-size: 24px; letter-spacing: 1px; }");
            html.append(".header .sub { font-size: 11px; color: #DCE8F3; font-weight: bold; letter-spacing: 1.5px; margin-top: 2px; }");
            html.append(".header .sis { font-size: 12px; color: #B4CADC; margin-top: 6px; }");

            html.append(".container { max-width: 650px; margin: 0 auto; padding: 14px; }");

            // Cards de Indicadores (KPIs)
            html.append(".kpi-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 14px; }");
            html.append(".kpi-card { background: white; padding: 14px; border-radius: 8px; border: 1px solid #CFDAE5; box-shadow: 0 2px 4px rgba(25,57,82,0.04); }");
            html.append(".kpi-title { font-size: 10px; font-weight: bold; color: #5E7489; text-transform: uppercase; margin-bottom: 4px; }");
            html.append(".kpi-val { font-size: 22px; font-weight: bold; }");
            html.append(".c-azul { color: #527D9D; } .c-verde { color: #669B83; } .c-vermelho { color: #B96C6C; } .c-dourado { color: #C39455; font-size: 18px; }");

            // Botões principais
            html.append(".btn-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 12px; }");
            html.append(".btn { width: 100%; border: none; border-radius: 8px; padding: 13px 10px; color: white; font-size: 13px; font-weight: bold; cursor: pointer; text-align: center; }");
            html.append(".btn-cam { background: #2980B9; }");
            html.append(".btn-cad { background: #669B83; }");

            html.append("#leitor-area { width: 100%; margin: 0 auto 14px auto; display: none; border-radius: 10px; overflow: hidden; border: 2px solid #193952; }");

            // Barra de pesquisa
            html.append(".search-box { background: white; padding: 10px; border-radius: 8px; border: 1px solid #CFDAE5; margin-bottom: 14px; }");
            html.append(".campo-busca { width: 100%; padding: 10px 12px; font-size: 15px; border-radius: 6px; border: 1px solid #B9CADA; outline: none; }");
            html.append(".campo-busca:focus { border-color: #193952; }");

            // Formulário de cadastro
            html.append(".box-form { display: none; background: white; padding: 18px; border-radius: 10px; border: 1px solid #CFDAE5; margin-bottom: 16px; box-shadow: 0 3px 10px rgba(0,0,0,0.06); }");
            html.append(".box-form h3 { margin: 0 0 12px 0; color: #193952; font-size: 16px; border-bottom: 1px solid #EEF4F9; padding-bottom: 8px; }");
            html.append(".label-form { display: block; font-size: 12px; font-weight: bold; color: #193952; margin-bottom: 4px; }");
            html.append(".input-form { width: 100%; padding: 10px; margin-bottom: 12px; border: 1px solid #B9CADA; border-radius: 6px; font-size: 14px; }");
            html.append(".row-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }");
            html.append(".btn-salvar { width: 100%; background: #669B83; color: white; padding: 13px; border: none; border-radius: 6px; font-size: 14px; font-weight: bold; cursor: pointer; }");

            // Cartões de peças
            html.append(".sec-titulo { font-size: 13px; font-weight: bold; color: #193952; margin: 6px 0 10px 2px; text-transform: uppercase; }");
            html.append(".card-artigo { background: white; border-radius: 8px; padding: 14px; margin-bottom: 10px; border: 1px solid #CFDAE5; border-left: 5px solid #527D9D; }");
            html.append(".card-critico { border-left: 5px solid #B96C6C; background: #FFFDFD; }");
            html.append(".card-topo { display: flex; justify-content: space-between; align-items: flex-start; gap: 8px; }");
            html.append(".cod-badge { background: #EEF4F9; color: #193952; font-size: 11px; font-weight: bold; padding: 3px 8px; border-radius: 4px; border: 1px solid #CFDAE5; }");
            html.append(".status-ok { background: #E8F5E9; color: #2E7D32; font-size: 10px; font-weight: bold; padding: 3px 8px; border-radius: 4px; }");
            html.append(".status-baixo { background: #FFEBEE; color: #C62828; font-size: 10px; font-weight: bold; padding: 3px 8px; border-radius: 4px; }");
            html.append(".titulo-peca { font-size: 16px; font-weight: bold; margin: 8px 0 4px 0; color: #193952; }");
            html.append(".detalhes { color: #5E7489; font-size: 12px; line-height: 1.5; }");
            html.append(".rodape-card { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; border-top: 1px solid #EEF4F9; padding-top: 10px; }");
            html.append(".quantidade { font-weight: bold; font-size: 14px; color: #193952; }");
            html.append(".btn-excluir { background: #B96C6C; color: white; border: none; padding: 7px 14px; border-radius: 6px; font-size: 12px; font-weight: bold; cursor: pointer; }");
            html.append("</style></head><body>");

            // Cabeçalho
            html.append("<div class='header'>");
            html.append("<h1>TDI</h1>");
            html.append("<div class='sub'>MÁQUINAS AGRÍCOLAS</div>");
            html.append("<div class='sis'>Sistema de Gestão de Almoxarifado (Acesso Mobile)</div>");
            html.append("</div>");

            html.append("<div class='container'>");

            // Indicadores
            html.append("<div class='kpi-grid'>");
            html.append("<div class='kpi-card'><div class='kpi-title'>Produtos Cadastrados</div><div class='kpi-val c-azul'>").append(totalProdutos).append("</div></div>");
            html.append("<div class='kpi-card'><div class='kpi-title'>Unidades em Estoque</div><div class='kpi-val c-verde'>").append(totalUnidades).append("</div></div>");
            html.append("<div class='kpi-card'><div class='kpi-title'>Alerta: Estoque Baixo</div><div class='kpi-val c-vermelho'>").append(totalBaixo).append("</div></div>");
            html.append("<div class='kpi-card'><div class='kpi-title'>Patrimônio em Estoque</div><div class='kpi-val c-dourado'>").append(formatoMoeda.format(valorPatrimonio)).append("</div></div>");
            html.append("</div>");

            // Botões de ação
            html.append("<div class='btn-grid'>");
            html.append("<button class='btn btn-cam' onclick='alternarCamera()'>📷 Ler QR Code (Câmera)</button>");
            html.append("<button class='btn btn-cad' onclick='toggleForm()'>➕ Cadastrar Nova Peça</button>");
            html.append("</div>");

            html.append("<div id='leitor-area'></div>");

            // Formulário de Cadastro
            html.append("<div id='formCadastro' class='box-form'>");
            html.append("<h3>Cadastrar Nova Peça no Banco MySQL</h3>");
            html.append("<form method='POST'>");
            html.append("<input type='hidden' name='acao' value='cadastrar'>");
            html.append("<label class='label-form'>Código da Peça / QR Code:</label>");
            html.append("<input type='text' id='codInput' name='codigo' class='input-form' placeholder='Ex: ROL-6205'>");
            html.append("<label class='label-form'>Descrição da Peça *:</label>");
            html.append("<input type='text' id='descInput' name='descricao' class='input-form' placeholder='Ex: Rolamento Esférico 6205' required>");
            html.append("<div class='row-2'>");
            html.append("<div><label class='label-form'>Quantidade Inicial *:</label><input type='number' name='quantidade' class='input-form' value='1' min='0' required></div>");
            html.append("<div><label class='label-form'>Estoque Mínimo *:</label><input type='number' name='minimo' class='input-form' value='5' min='0' required></div>");
            html.append("</div>");
            html.append("<div class='row-2'>");
            html.append("<div><label class='label-form'>Localização / Prateleira:</label><input type='text' name='localizacao' class='input-form' placeholder='Ex: Corredor A'></div>");
            html.append("<div><label class='label-form'>Custo Unitário (R$):</label><input type='text' name='custo' class='input-form' placeholder='0,00'></div>");
            html.append("</div>");
            html.append("<button type='submit' class='btn-salvar'>SALVAR PEÇA NO ESTOQUE</button>");
            html.append("</form>");
            html.append("</div>");

            // Barra de busca
            html.append("<div class='search-box'>");
            html.append("<input type='text' id='barraBusca' class='campo-busca' placeholder='🔍 Pesquisar por código, nome ou localização...' onkeyup='filtrarArtigos()'>");
            html.append("</div>");

            html.append("<div class='sec-titulo'>Itens no Almoxarifado (Sincronizado com MySQL)</div>");

            html.append("<div id='listaArtigos'>");
            if (erroBanco != null) {
                html.append("<div style='background:#FFEBEE; color:#C62828; padding:12px; border-radius:8px; font-size:13px;'>Erro ao conectar no MySQL: ").append(erroBanco).append("</div>");
            } else if (lista != null) {
                for (Produto p : lista) {
                    boolean critico = p.getQuantidadeEstoque() <= p.getEstoqueMinimo();
                    String codTexto = (p.getCodigoPeca() != null) ? p.getCodigoPeca() : ("ID-" + p.getIdProduto());
                    String termoBusca = (codTexto + " " + p.getDescricao() + " " + p.getNomeCategoria() + " " + p.getLocalizacaoPrateleira()).toLowerCase();

                    html.append("<div class='card-artigo ").append(critico ? "card-critico" : "").append("' data-termo='").append(termoBusca).append("'>");
                    html.append("<div class='card-topo'>");
                    html.append("<span class='cod-badge'>").append(codTexto).append("</span>");
                    if (critico) {
                        html.append("<span class='status-baixo'>ESTOQUE BAIXO (Mín: ").append(p.getEstoqueMinimo()).append(")</span>");
                    } else {
                        html.append("<span class='status-ok'>NORMAL</span>");
                    }
                    html.append("</div>");

                    html.append("<div class='titulo-peca'>").append(p.getDescricao()).append("</div>");
                    html.append("<div class='detalhes'>Categoria: ").append(p.getNomeCategoria())
                        .append(" • Local: ").append(p.getLocalizacaoPrateleira() != null ? p.getLocalizacaoPrateleira() : "Geral")
                        .append(" • Unit: ").append(formatoMoeda.format(p.getPrecoCusto())).append("</div>");

                    html.append("<div class='rodape-card'>");
                    html.append("<span class='quantidade'>Saldo Atual: ").append(p.getQuantidadeEstoque()).append(" unid.</span>");

                    html.append("<form method='POST' style='margin:0;' onsubmit='return confirm(\"Deseja realmente excluir a peça: ").append(p.getDescricao().replace("'", "\\'")).append("?\");'>");
                    html.append("<input type='hidden' name='acao' value='excluir'>");
                    html.append("<input type='hidden' name='id' value='").append(p.getIdProduto()).append("'>");
                    html.append("<button type='submit' class='btn-excluir'>Excluir</button>");
                    html.append("</form>");

                    html.append("</div>");
                    html.append("</div>");
                }
            }
            html.append("</div>");
            html.append("</div>");

            // JavaScript de Filtro e Leitor de QR Code
            html.append("<script>");
            html.append("function toggleForm() {");
            html.append("  var f = document.getElementById('formCadastro');");
            html.append("  f.style.display = (f.style.display === 'block') ? 'none' : 'block';");
            html.append("}");

            html.append("function filtrarArtigos() {");
            html.append("  var valor = document.getElementById('barraBusca').value.toLowerCase();");
            html.append("  var cartoes = document.getElementsByClassName('card-artigo');");
            html.append("  var achou = false;");
            html.append("  for (var i = 0; i < cartoes.length; i++) {");
            html.append("    var termo = cartoes[i].getAttribute('data-termo');");
            html.append("    var bateu = termo.includes(valor);");
            html.append("    cartoes[i].style.display = bateu ? 'block' : 'none';");
            html.append("    if (bateu) achou = true;");
            html.append("  }");
            html.append("  if (!achou && valor.trim().length > 0) {");
            html.append("    document.getElementById('formCadastro').style.display = 'block';");
            html.append("    document.getElementById('codInput').value = document.getElementById('barraBusca').value;");
            html.append("  }");
            html.append("}");

            html.append("var qrScanner = null;");
            html.append("function alternarCamera() {");
            html.append("  var container = document.getElementById('leitor-area');");
            html.append("  if (container.style.display === 'block') {");
            html.append("    if (qrScanner) { qrScanner.stop().then(function() { container.style.display = 'none'; }); }");
            html.append("    return;");
            html.append("  }");
            html.append("  container.style.display = 'block';");
            html.append("  qrScanner = new Html5Qrcode('leitor-area');");
            html.append("  var config = { fps: 15, qrbox: { width: 250, height: 250 } };");
            html.append("  qrScanner.start({ facingMode: 'environment' }, config, function(codigoLido) {");
            html.append("    document.getElementById('barraBusca').value = codigoLido;");
            html.append("    filtrarArtigos();");
            html.append("    qrScanner.stop().then(function() { container.style.display = 'none'; });");
            html.append("  }, function(err) {}).catch(function(erro) {");
            html.append("    alert('Erro ao abrir câmera: ' + erro);");
            html.append("    container.style.display = 'none';");
            html.append("  });");
            html.append("}");
            html.append("</script>");

            html.append("</body></html>");

            byte[] bytes = html.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private Map<String, String> parseForm(String formData) {
            Map<String, String> map = new HashMap<>();
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    try {
                        String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8.name());
                        String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name());
                        map.put(key, value);
                    } catch (Exception ignored) {
                    }
                }
            }
            return map;
        }
    }
}
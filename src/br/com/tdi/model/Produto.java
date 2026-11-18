package br.com.tdi.model;

public class Produto {
    private int idProduto;
    private String codigoPeca;
    private String descricao;
    private int idCategoria;
    private String nomeCategoria;
    private int quantidadeEstoque;
    private int estoqueMinimo;
    private String localizacaoPrateleira;
    private double precoCusto;

    public Produto() {}

    public Produto(String codigoPeca, String descricao, int idCategoria, int quantidadeEstoque, int estoqueMinimo, String localizacaoPrateleira, double precoCusto) {
        this.codigoPeca = codigoPeca;
        this.descricao = descricao;
        this.idCategoria = idCategoria;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueMinimo = estoqueMinimo;
        this.localizacaoPrateleira = localizacaoPrateleira;
        this.precoCusto = precoCusto;
    }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public String getCodigoPeca() { return codigoPeca; }
    public void setCodigoPeca(String codigoPeca) { this.codigoPeca = codigoPeca; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }

    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }

    public int getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public String getLocalizacaoPrateleira() { return localizacaoPrateleira; }
    public void setLocalizacaoPrateleira(String localizacaoPrateleira) { this.localizacaoPrateleira = localizacaoPrateleira; }

    public double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(double precoCusto) { this.precoCusto = precoCusto; }
}

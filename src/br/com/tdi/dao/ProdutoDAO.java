package br.com.tdi.dao;

import br.com.tdi.conexao.ConexaoBD;
import br.com.tdi.model.Produto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public boolean salvar(Produto p) {
        String sql = "INSERT INTO produtos (codigo_peca, descricao, id_categoria, quantidade_estoque, estoque_minimo, localizacao_prateleira, preco_custo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getCodigoPeca());
            ps.setString(2, p.getDescricao());
            ps.setInt(3, p.getIdCategoria());
            ps.setInt(4, p.getQuantidadeEstoque());
            ps.setInt(5, p.getEstoqueMinimo());
            ps.setString(6, p.getLocalizacaoPrateleira());
            ps.setDouble(7, p.getPrecoCusto());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar peça: " + e.getMessage());
            return false;
        }
    }

    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.nome AS nome_categoria FROM produtos p " +
                     "JOIN categorias c ON p.id_categoria = c.id_categoria ORDER BY p.descricao ASC";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Produto p = new Produto();
                p.setIdProduto(rs.getInt("id_produto"));
                p.setCodigoPeca(rs.getString("codigo_peca"));
                p.setDescricao(rs.getString("descricao"));
                p.setIdCategoria(rs.getInt("id_categoria"));
                p.setNomeCategoria(rs.getString("nome_categoria"));
                p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                p.setEstoqueMinimo(rs.getInt("estoque_minimo"));
                p.setLocalizacaoPrateleira(rs.getString("localizacao_prateleira"));
                p.setPrecoCusto(rs.getDouble("preco_custo"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar peças: " + e.getMessage());
        }
        return lista;
    }

    public boolean excluir(int idProduto) {
        String sql = "DELETE FROM produtos WHERE id_produto = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProduto);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir peça: " + e.getMessage());
            return false;
        }
    }
}

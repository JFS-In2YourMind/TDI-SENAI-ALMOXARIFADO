package br.com.tdi.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String URL = "jdbc:mysql://localhost:3306/tdi_almoxarifado?useTimezone=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "SENAI";

    public static Connection getConexao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC do MySQL não encontrado: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao conectar à base de dados: " + e.getMessage());
            return null;
        }
    }
}

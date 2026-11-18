Markdown# TDI Máquinas Agrícolas - Sistema de Gestão de Almoxarifado

Sistema desktop profissional desenvolvido em **Java (Swing / NetBeans)** com banco de dados **MySQL** para o controle de estoque, cadastro de peças, monitoramento de itens críticos e movimentações de entrada e saída.

Trabalho acadêmico desenvolvido pela equipe **InfinityCode** para o curso de Análise e Desenvolvimento de Sistemas do **SENAI**.

---

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java (JDK 8+)
* **Interface Gráfica:** Java Swing (FlatLaf / LookAndFeel nativo)
* **Banco de Dados:** MySQL (via JDBC)
* **Bibliotecas e Recursos:**
  * Leitor de QR Code / Código de Barras (ZXing Core / JavaSE)
  * Captura de Câmera ao Vivo (Webcam Capture API)
  * Gerenciamento de Logs e Alertas em Tempo Real

---

## 📋 Funcionalidades do Sistema

1. **Autenticação de Usuários:** Tela de login e cadastro com validação direta no banco de dados MySQL.
2. **Dashboard Dinâmico:** Visão geral com indicadores em tempo real (total de produtos, unidades em estoque, valor patrimonial e alerta de itens críticos).
3. **Consulta e Filtros:** Busca avançada de peças por texto ou leitura ótica via webcam.
4. **Cadastro de Peças:** Registro de novos itens com código, descrição, categoria, localização na prateleira, quantidade inicial, estoque mínimo e preço de custo.
5. **Entrada e Saída de Materiais:** Movimentação de estoque vinculada a Fornecedores (Entradas) e Clientes (Saídas), gerando histórico e baixas automáticas.
6. **Relatórios e Exportação:** Relatório financeiro consolidado e exportação de dados para arquivos `.txt`.
7. **Leitor de QR Code:** Suporte a leitura por câmera ao vivo (Webcam) ou carregamento de imagens do computador.

---

## 🛠️ Como Executar o Projeto

### Pré-requisitos
* **Java JDK** instalado na máquina.
* **MySQL Server** configurado localmente.
* **NetBeans IDE** (recomendado para abrir e compilar o projeto).

### Passo a Passo
1. Clone este repositório ou baixe o projeto compactado:
   ```bash
   git clone [https://github.com/JFS-In2YourMind/TDI-SENAI-ALMOXARIFADO.git](https://github.com/JFS-In2YourMind/TDI-SENAI-ALMOXARIFADO.git)
Importe o banco de dados utilizando o arquivo de Dump (Dump20261711.sql) no seu MySQL Workbench.Abra a pasta do projeto no NetBeans.Certifique-se de que as bibliotecas da pasta lib estão adicionadas corretamente nas Libraries do projeto.Execute o arquivo principal pressionando F6.

👥 Equipe de Desenvolvimento (InfinityCode)IntegrantePerfil 
GitHub / ContatoJaqueline Felix @JFS-In2YourMind
Sarah Acacio @sarahacacio
Gabriel GraçaEquipe InfinityCode (SENAI)  
Kauan SilvaEquipe InfinityCode (SENAI)  
Raphael FidelisEquipe InfinityCode (SENAI)

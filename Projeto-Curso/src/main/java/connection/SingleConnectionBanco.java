package connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class SingleConnectionBanco { //Conexão com banco é somente uma,  as demais são sessões ou translações.
	
	
	private static String banco = "jdbc:postgresql://localhost:5432/burno3778_projeto1?autoReconnect=true";
	private static String user = "burno3778";
	private static String senha = "fGmr9RIdMDH9uiO"; 
	private static Connection connection = null;
	
	
	public static Connection getConnection() {
		return connection;
	}
	
	static {
		conectar();
	}
	
	public SingleConnectionBanco() { //Quando tiver uma instancia vai conectar
		conectar(); // Permite que faça instanciação pra qualquer lugar
	}
	
	
	private static void conectar () {
		try {
			
			if (connection == null) {
				Class.forName("org.postgresql.Driver"); ///Carrega o drive de conexão do banco
				connection = DriverManager.getConnection(banco, user, senha);
				connection.setAutoCommit(false); //Para nao efetiar operações no banco sem nosso comando
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace(); //Mostrar qualquer errono no momento de conectar
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

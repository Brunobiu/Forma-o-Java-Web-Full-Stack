package filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import connection.SingleConnectionBanco;
import dao.DaoVersionadorBanco;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = { "/principal/*" }) // Intercepta todas as requisiçoes que vierem do projeto ou mapeamento
public class FilterAutenticacao extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;

	// Declaração do banco de dados
	private static Connection connection;

	public FilterAutenticacao() {
		super();
	}

	// Encerra os processos quando parar o servidor
	public void destroy() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {

			HttpServletRequest req = (HttpServletRequest) request;
			HttpSession session = req.getSession();

			String usuarioLogado = (String) session.getAttribute("usuario");

			String urlParaAutenticar = req.getServletPath(); // url que esta sendo acessada

			// Validar se esta logado se não redireciona pra a tela de login
			if (usuarioLogado == null && !urlParaAutenticar.equalsIgnoreCase("/principal/ServletLogin")) { // Não esta
																											// logado

				RequestDispatcher redireciona = request.getRequestDispatcher("/index.jsp?url=" + urlParaAutenticar);
				request.setAttribute("msg", "Por favor realize o login!");
				redireciona.forward(request, response);
				return; // parar a execuxão e redireciona para o login

			} else {
				// deixa o processo continuar
				chain.doFilter(request, response);
			}

			connection.commit(); // Deu tudo certo então comita as alterações no banco de dados

		} catch (Exception e) {
			e.printStackTrace();

			RequestDispatcher redirecioanar = request.getRequestDispatcher("erro.jsp");
			request.setAttribute("msg", e.getMessage());
			redirecioanar.forward(request, response);

			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	// inicia a conexão com banco de dados
	public void init(FilterConfig fConfig) throws ServletException {
		connection = SingleConnectionBanco.getConnection();

		// Metodo de Versionamento com banco de dados.
		DaoVersionadorBanco daoVersionadorBanco = new DaoVersionadorBanco();

		String caminhoPastaSql = fConfig.getServletContext().getRealPath("versionadorbancosql") + File.separator;

		File[] filessSql = new File(caminhoPastaSql).listFiles();

		try {

			for (File file : filessSql) {

				boolean arquivoJaRodado = daoVersionadorBanco.arquivoSqlRodado(file.getName());

				if (!arquivoJaRodado) {

					FileInputStream entradaArquivo = new FileInputStream(file);

					Scanner lerAquivo = new Scanner(entradaArquivo, "UTF-8");

					StringBuilder sql = new StringBuilder();
					while (lerAquivo.hasNext()) {

						sql.append(lerAquivo.hasNextLine());
						sql.append("\n");

					}

					connection.prepareStatement(sql.toString()).execute();
					daoVersionadorBanco.gravaArquivoSqlRodado(file.getName());

					connection.commit();
					lerAquivo.close();

				}
			}

		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

}

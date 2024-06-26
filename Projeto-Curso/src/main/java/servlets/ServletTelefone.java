package servlets;

import java.io.IOException;
import java.util.List;

import dao.DAOTelefoneRepository;
import dao.DAOUsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ModelLogin;
import model.ModelTelefone;

@WebServlet("/ServletTelefone")
public class ServletTelefone extends ServletGenericUtil {
	private static final long serialVersionUID = 1L;

	private DAOUsuarioRepository daoUsuarioRepository = new DAOUsuarioRepository();
	
	private DAOTelefoneRepository daoTelefoneRepository = new DAOTelefoneRepository();

	public ServletTelefone() {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			//Deletar telefone
			String acao = request.getParameter("acao");
			
			if(acao != null && !acao.isEmpty() && acao.equals("excluir")) {
				
				String idFone = request.getParameter("id");
				
				//Deletar
				daoTelefoneRepository.deleteFone(Long.parseLong(idFone));
				
				String userpai = request.getParameter("userpai");
				
				ModelLogin modelLogin = daoUsuarioRepository.consultaUsuarioID(Long.parseLong(userpai));
				
				List<ModelTelefone> modelTelefones = daoTelefoneRepository.listFone(modelLogin.getId());
				request.setAttribute("modelTelefones", modelTelefones);
				
				request.setAttribute("modelLogin", modelLogin);
				request.getRequestDispatcher("principal/telefone.jsp").forward(request, response);

				
				return; //Não deixa o bloco a baixo executar quando estiver exexutando o de cima
				
			}

			String iduser = request.getParameter("iduser");

			if (iduser != null && !iduser.isEmpty()) {

				ModelLogin modelLogin = daoUsuarioRepository.consultaUsuarioID(Long.parseLong(iduser));
				
				List<ModelTelefone> modelTelefones = daoTelefoneRepository.listFone(modelLogin.getId());
				request.setAttribute("modelTelefones", modelTelefones);
				
				request.setAttribute("modelLogin", modelLogin);
				request.getRequestDispatcher("principal/telefone.jsp").forward(request, response);

			} else {
				List<ModelLogin> modelLogins = daoUsuarioRepository.consultaUsuarioList(super.getUserLogado(request));
				request.setAttribute("modelLogins", modelLogins);
				request.setAttribute("totalpagina", daoUsuarioRepository.totoalPagina(this.getUserLogado(request)));
				request.getRequestDispatcher("principal/usuario.jsp").forward(request, response);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		try {
			String idusuario_pai_id = request.getParameter("id");
			String numero = request.getParameter("numero");
			
			
			if (!daoTelefoneRepository.existeFone(numero, Long.valueOf(idusuario_pai_id))) {
			
				ModelTelefone modelTelefone = new ModelTelefone();
				
				modelTelefone.setNumero(numero);
				modelTelefone.setUsuario_pai_id(daoUsuarioRepository.consultaUsuarioID(Long.parseLong(idusuario_pai_id)));
				modelTelefone.setUsuario_cad_id(super.getUserLogadoObjt(request));
				
				daoTelefoneRepository.gravaTelefone(modelTelefone);
				
				
				request.setAttribute("msg", "Salvo com sucesso!");
			
			}else {
				request.setAttribute("msg", "Telefone já existe!");
			}
			
				
				List<ModelTelefone> modelTelefones = daoTelefoneRepository.listFone(Long.parseLong(idusuario_pai_id));
				
				//Faz os retorno na tela
				ModelLogin modelLogin = daoUsuarioRepository.consultaUsuarioID(Long.parseLong(idusuario_pai_id));
				
				request.setAttribute("modelLogin", modelLogin);
				request.setAttribute("modelTelefones", modelTelefones);
				request.getRequestDispatcher("principal/telefone.jsp").forward(request, response);
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}

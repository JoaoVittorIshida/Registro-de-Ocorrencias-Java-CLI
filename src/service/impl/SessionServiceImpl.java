package service.impl;

import dao.api.IFuncionarioDAO;
import model.Funcionario;
import service.api.ISessionService;
import session.SessionManager;

public class SessionServiceImpl implements ISessionService {

    private final IFuncionarioDAO funcionarioDAO;

    public SessionServiceImpl(IFuncionarioDAO funcionarioDAO) {
        this.funcionarioDAO = funcionarioDAO;
    }

    
    //Sistema de login, realiza validações se a matricula existe e define o tipo de usuário no SessionManager, muito importante para validações futuras.
    
    @Override
    public void login(int matricula) throws Exception {
        Funcionario usuario = funcionarioDAO.buscarPorMatricula(matricula);
        if (usuario == null) {
            throw new Exception("Matrícula não encontrada.");
        }
        if (!"Ativo".equalsIgnoreCase(usuario.getStatus())) {
            throw new Exception("Usuário não está ativo no sistema.");
        }
        SessionManager.setUsuarioLogado(usuario);
    }

    //Logout limpa o SessionManager
    
    @Override
    public void logout() {
        SessionManager.clearSession();
    }
}
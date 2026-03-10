package service.impl;

import java.util.Collections;
import java.util.List;

import dao.api.IDepartamentoDAO;
import exception.AuthException;
import model.Departamento;
import model.Diretor;
import service.api.IDepartamentoService;
import session.SessionManager;

public class DepartamentoServiceImpl implements IDepartamentoService {

    private final IDepartamentoDAO departamentoDAO;

    public DepartamentoServiceImpl(IDepartamentoDAO departamentoDAO) {
        this.departamentoDAO = departamentoDAO;
    }

    @Override
    public void criarDepartamento(Departamento departamento) throws AuthException, Exception {
    	
    	//Verifica se o usuário logado é um Diretor, depois prossegue com a criação do Departamento.
        if (!(SessionManager.getUsuarioLogado() instanceof Diretor)) {
            throw new AuthException("Apenas diretores podem realizar esta operação.");
        }
        departamentoDAO.salvar(departamento);
    }

    @Override
    public void alterarDepartamento(Departamento departamento) throws AuthException, Exception {
    	
    	//Verifica se o usuário logado é um Diretor, depois prossegue com a atualização do Departamento.
        if (!(SessionManager.getUsuarioLogado() instanceof Diretor)) {
            throw new AuthException("Apenas diretores podem realizar esta operação.");
        }
        departamentoDAO.atualizar(departamento);
    }

    @Override
    public void deletarDepartamento(int codigo) throws AuthException, Exception {
    	
    	//Verifica se o usuário logado é um Diretor, depois prossegue com a exclusão do Departamento.
        if (!(SessionManager.getUsuarioLogado() instanceof Diretor)) {
            throw new AuthException("Apenas diretores podem excluir departamentos.");
        }
        departamentoDAO.deletar(codigo);
    }

    @Override
    public Departamento buscarPorId(int id) throws Exception {
    	
    	//Retorna o departamento de acordo com o ID.
        return departamentoDAO.buscarPorId(id);
    }

    @Override
    public List<Departamento> listarDepartamentos() {
    	
    	//Lista todos os departamentos.
        try {
            return departamentoDAO.buscarTodos();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
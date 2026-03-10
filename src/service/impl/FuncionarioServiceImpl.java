package service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dao.api.IFuncionarioDAO;
import exception.AuthException;
import model.Diretor;
import model.Funcionario;
import model.Gerente;
import service.api.IFuncionarioService;
import session.SessionManager;

public class FuncionarioServiceImpl implements IFuncionarioService {

    private final IFuncionarioDAO funcionarioDAO;

    public FuncionarioServiceImpl(IFuncionarioDAO funcionarioDAO) {
        this.funcionarioDAO = funcionarioDAO;
    }

    @Override
    public void criarFuncionario(Funcionario funcionario) throws AuthException, Exception {
        Funcionario autor = SessionManager.getUsuarioLogado();

        // Todos os usuários são tratados como funcionário devido ao polimorfismo.
        // Então, a permissão é verificada de maneira sequencial, verificando o tipo de autor e o tipo de funcionario a ser criado.
        if (funcionario instanceof Gerente) {
            if (!(autor instanceof Diretor)) {
                throw new AuthException("Apenas diretores podem cadastrar novos gerentes.");
            }
        } 
        else {
            if (!(autor instanceof Gerente)) {
                throw new AuthException("Apenas gerentes podem cadastrar funcionários comuns.");
            }
        }

        // Se passou pelas regras de permissão, salva no banco.
        funcionarioDAO.salvar(funcionario);
    }
    
    @Override
    public void alterarFuncionario(Funcionario funcionario) throws AuthException, Exception {
    	
        Funcionario autor = SessionManager.getUsuarioLogado();
        Funcionario alvo = funcionarioDAO.buscarPorMatricula(funcionario.getMatricula());
        
    	//Validações para garantir que permissões sejam respeitadas e que gerentes possam alterar apenas próprios funcionários.

        if(alvo == null) throw new Exception("Funcionário a ser alterado não encontrado.");

        if (alvo instanceof Gerente || alvo instanceof Diretor) {
            if (!(autor instanceof Diretor)) { throw new AuthException("Apenas diretores podem alterar gestores."); }
        } else {
            if (!(autor instanceof Gerente)) { throw new AuthException("Apenas gerentes podem alterar funcionários."); }
            if (!Objects.equals(autor.getDepartamento().getCodigo(), alvo.getDepartamento().getCodigo())) {
                throw new AuthException("Um gerente só pode alterar funcionários do seu próprio departamento.");
            }
        }
        funcionarioDAO.atualizar(funcionario);
    }

    @Override
    public void deletarFuncionario(int matricula) throws AuthException, Exception {
        Funcionario autor = SessionManager.getUsuarioLogado();
        Funcionario funcionarioASerDeletado = funcionarioDAO.buscarPorMatricula(matricula);
        
        //Executa validações necessárias para prosseguir com exclusão de funcionário,

        if (funcionarioASerDeletado == null) { throw new Exception("Funcionário com matrícula " + matricula + " não encontrado."); }
        if (autor.getMatricula() == matricula) { throw new Exception("Você não pode excluir a si mesmo."); }

        if (funcionarioASerDeletado instanceof Gerente || funcionarioASerDeletado instanceof Diretor) {
            if (!(autor instanceof Diretor)) { throw new AuthException("Apenas diretores podem excluir gestores."); }
        } else {
            if (!(autor instanceof Gerente)) { throw new AuthException("Apenas gerentes podem excluir funcionários."); }
            if (!Objects.equals(autor.getDepartamento().getCodigo(), funcionarioASerDeletado.getDepartamento().getCodigo())) {
                throw new AuthException("Um gerente só pode excluir funcionários do seu próprio departamento.");
            }
        }
        funcionarioDAO.deletar(matricula);
    }

    @Override
    public Funcionario buscarPorMatricula(int matricula) throws Exception {
    	//Retorna funcionário de acordo com a matricula passada
        return funcionarioDAO.buscarPorMatricula(matricula);
    }

    @Override
    public List<Funcionario> listarTodos() {
    	//Retorna lista com todos os funcionarios
        try { return funcionarioDAO.buscarTodos(); } 
        catch (Exception e) { e.printStackTrace(); return Collections.emptyList(); }
    }

    @Override
    public List<Funcionario> listarFuncionariosDoMeuDepartamento() throws AuthException, Exception {
        Funcionario usuarioLogado = SessionManager.getUsuarioLogado();
        
        //Lista todos os funcionários com base no departamento de quem está logado.
        if (!(usuarioLogado instanceof Gerente)) {
            throw new AuthException("Esta função é exclusiva para gerentes.");
        }
        Gerente autor = (Gerente) usuarioLogado;
        return funcionarioDAO.buscarPorDepartamento(autor.getDepartamento().getCodigo());
    }
}

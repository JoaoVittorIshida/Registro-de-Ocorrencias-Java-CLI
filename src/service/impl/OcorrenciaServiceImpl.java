package service.impl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dao.api.IFuncionarioDAO;
import dao.api.IOcorrenciaDAO;
import exception.AuthException;
import model.Funcionario;
import model.Gerente;
import model.Ocorrencia;
import service.api.IOcorrenciaService;
import session.SessionManager;

public class OcorrenciaServiceImpl implements IOcorrenciaService {

    private final IOcorrenciaDAO ocorrenciaDAO;
    private final IFuncionarioDAO funcionarioDAO;

    public OcorrenciaServiceImpl(IOcorrenciaDAO ocorrenciaDAO, IFuncionarioDAO funcionarioDAO) {
        this.ocorrenciaDAO = ocorrenciaDAO;
        this.funcionarioDAO = funcionarioDAO;
    }

    @Override
    public void registrarOcorrencia(Ocorrencia ocorrencia) throws AuthException, Exception {
    	
    	// Primeiro verifica se é um gerente, depois executa uma série de validações para verificar se é uma ocorrencia válida.
    	
        Funcionario usuarioLogado = SessionManager.getUsuarioLogado();
        
        if (!(usuarioLogado instanceof Gerente)) {
            throw new AuthException("Apenas gerentes podem registrar ocorrências.");
        }
        
        if (!Objects.equals(usuarioLogado.getDepartamento().getCodigo(), ocorrencia.getDepartamentoReportante().getCodigo())) {
            throw new AuthException("Um gerente só pode abrir ocorrências para o seu próprio departamento.");
        }
  
        if (ocorrencia.getDataOcorrencia().isAfter(LocalDate.now())) {
            throw new Exception("A data da ocorrência não pode ser futura.");
        }
        
        if (!ocorrencia.getDataLimiteSolucao().isAfter(LocalDate.now())) {
            throw new Exception("A data limite para solução deve ser uma data futura.");
        }

        Funcionario alocado = funcionarioDAO.buscarPorMatricula(ocorrencia.getFuncionarioAlocado().getMatricula());
        if (alocado == null || !"Informática".equalsIgnoreCase(alocado.getDepartamento().getNome())) {
            throw new Exception("Funcionário alocado é inválido ou não pertence ao depto de Informática.");
        }
        
        ocorrencia.setStatusTemporario("Aberta");
        ocorrencia.setStatusDefinitivo("Aberta");

        ocorrenciaDAO.salvar(ocorrencia);
    }

    @Override
    public void encerrarOcorrenciaTemporariamente(int numeroOcorrencia) throws AuthException, Exception {
    	
    	//Busca a ocorrencia, caso encontre, prossegue a verificar se é pertencente a aquele funcionario que está tentando encerrar a ocorrencia.
        Ocorrencia ocorrencia = ocorrenciaDAO.buscarPorNumero(numeroOcorrencia);
        if (ocorrencia == null) throw new Exception("Ocorrência não encontrada.");

        if (ocorrencia.getFuncionarioAlocado().getMatricula() != SessionManager.getUsuarioLogado().getMatricula()) {
            throw new AuthException("Você não é o funcionário alocado para esta ocorrência.");
        }
        
        ocorrenciaDAO.atualizarStatus(numeroOcorrencia, "Encerrada", ocorrencia.getStatusDefinitivo());
    }

    @Override
    public void fecharOcorrenciaDefinitivamente(int numeroOcorrencia) throws AuthException, Exception {
    	
    	//Busca número da ocorrencia e executa validações para verificar se o comando partiu de um gerente.
        Ocorrencia ocorrencia = ocorrenciaDAO.buscarPorNumero(numeroOcorrencia);
        if (ocorrencia == null) throw new Exception("Ocorrência não encontrada.");

        Funcionario usuarioLogado = SessionManager.getUsuarioLogado();
        if (!(usuarioLogado instanceof Gerente)) {
            throw new AuthException("Apenas gerentes podem fechar ocorrências definitivamente.");
        }
        if (usuarioLogado.getDepartamento().getCodigo() != ocorrencia.getDepartamentoReportante().getCodigo()) {
            throw new AuthException("Você não é o gerente do departamento que abriu esta ocorrência.");
        }
        
        ocorrenciaDAO.atualizarStatus(numeroOcorrencia, ocorrencia.getStatusTemporario(), "Encerrada");
    }
    
    @Override
    public List<Ocorrencia> consultarOcorrenciasDoMeuDepartamento() throws AuthException, Exception {
    	
        // Valida se usuário é um gerente, depois retorna a busca de ocorrencias pelo departamento do gerente logado.
        Funcionario usuarioLogado = SessionManager.getUsuarioLogado();
        if (!(usuarioLogado instanceof Gerente)) {
            throw new AuthException("Apenas gerentes podem visualizar as ocorrências do departamento.");
        }

        Gerente gerente = (Gerente) usuarioLogado;
        int idDepartamento = gerente.getDepartamento().getCodigo();
        
        return ocorrenciaDAO.buscarPorDepartamentoReportante(idDepartamento);
    }

    @Override
    public List<Ocorrencia> consultarMinhasOcorrencias() throws AuthException {
    	
    	//Verifica se tem usuário logado, em seguida busca ocorrencias com base na matricula.
        Funcionario usuarioLogado = SessionManager.getUsuarioLogado();
        if (usuarioLogado == null) throw new AuthException("Nenhum usuário logado.");
        
        try {
            return ocorrenciaDAO.buscarPorFuncionario(usuarioLogado.getMatricula());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}

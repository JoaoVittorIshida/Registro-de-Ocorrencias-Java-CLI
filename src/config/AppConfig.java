package config;

import dao.api.IDepartamentoDAO;
import dao.api.IFuncionarioDAO;
import dao.api.IOcorrenciaDAO;
import dao.impl.DepartamentoDAOImpl;
import dao.impl.FuncionarioDAOImpl;
import dao.impl.OcorrenciaDAOImpl;
import service.api.IDepartamentoService;
import service.api.IFuncionarioService;
import service.api.IOcorrenciaService;
import service.api.ISessionService;
import service.impl.DepartamentoServiceImpl;
import service.impl.FuncionarioServiceImpl;
import service.impl.OcorrenciaServiceImpl;
import service.impl.SessionServiceImpl;

public class AppConfig {

    // --- 1. INSTANCIAÇÃO DOS DAOs ---
    // Criamos uma única instância de cada DAO.
    private static final IDepartamentoDAO departamentoDAO = new DepartamentoDAOImpl();
    private static final IFuncionarioDAO funcionarioDAO = new FuncionarioDAOImpl();
    private static final IOcorrenciaDAO ocorrenciaDAO = new OcorrenciaDAOImpl();
    

    // --- 2. INSTANCIAÇÃO DOS SERVIÇOS (com injeção de dependências) ---
    // Criamos as instâncias dos serviços, passando os DAOs necessários em seus construtores.
    private static final ISessionService sessionService = new SessionServiceImpl(funcionarioDAO);
    private static final IDepartamentoService departamentoService = new DepartamentoServiceImpl(departamentoDAO);
    private static final IFuncionarioService funcionarioService = new FuncionarioServiceImpl(funcionarioDAO);
    private static final IOcorrenciaService ocorrenciaService = new OcorrenciaServiceImpl(ocorrenciaDAO, funcionarioDAO);


    // --- 3. MÉTODOS PÚBLICOS (Getters) ---
    // A aplicação usará estes métodos para obter acesso aos serviços.
    public static ISessionService getSessionService() {
        return sessionService;
    }
    
    public static IDepartamentoService getDepartamentoService() {
        return departamentoService;
    }

    public static IFuncionarioService getFuncionarioService() {
        return funcionarioService;
    }

    public static IOcorrenciaService getOcorrenciaService() {
        return ocorrenciaService;
    }
    
    public static IOcorrenciaDAO getOcorrenciaDAO() {
        return ocorrenciaDAO;
    }
}

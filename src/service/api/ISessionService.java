package service.api;

public interface ISessionService {
	
	//Interface simples para Service de Sessão.
	
    void login(int matricula) throws Exception;
    void logout();
}

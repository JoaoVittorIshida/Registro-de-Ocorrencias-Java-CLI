package service.api;

import java.util.List;

import exception.AuthException;
import model.Ocorrencia;

public interface IOcorrenciaService {
	
	//Interface do OcorrenciaService, registra os métodos que devemos ter na OcorrenciaService.

	
    void registrarOcorrencia(Ocorrencia ocorrencia) throws AuthException, Exception;
    void encerrarOcorrenciaTemporariamente(int numeroOcorrencia) throws AuthException, Exception;
    void fecharOcorrenciaDefinitivamente(int numeroOcorrencia) throws AuthException, Exception;
    List<Ocorrencia> consultarMinhasOcorrencias() throws AuthException;
    List<Ocorrencia> consultarOcorrenciasDoMeuDepartamento() throws AuthException, Exception;

}

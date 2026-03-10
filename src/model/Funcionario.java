package model;

public class Funcionario {

	//Model de funcionário, é a base para gerar nossos funcionarios comuns, gerentes e diretores.
	//Além dos parâmetros, contém um método que retorna em string formatada algumas informações do objeto.
	
    private int matricula;
    private String nome;
    private String status;
    private Departamento departamento;

    public Funcionario() { }

    // Getters e Setters
    public int getMatricula() { return matricula; }
    public void setMatricula(int matricula) { this.matricula = matricula; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    @Override
    public String toString() {
        return String.format("Matrícula: %d, Nome: %s, Status: %s", matricula, nome, status);
    }
}

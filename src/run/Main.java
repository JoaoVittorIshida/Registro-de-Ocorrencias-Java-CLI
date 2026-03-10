package run;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import config.AppConfig;
import exception.AuthException;
import model.Departamento;
import model.Diretor;
import model.Funcionario;
import model.Gerente;
import model.Ocorrencia;
import service.api.IDepartamentoService;
import service.api.IFuncionarioService;
import service.api.IOcorrenciaService;
import service.api.ISessionService;
import session.SessionManager;


//Esta classe é a nossa interface com o usuário no console.
//Ela é responsável por:
//1. Mostrar os menus na tela.
//2. Ler o que o usuário digita (usando o Scanner).
//3. Chamar o Serviço apropriado (que ela pega do AppConfig) para fazer o trabalho de verdade.
//4. Cuidar do fluxo de login e mostrar o menu certo para cada tipo de usuário.
//
//Ela NÃO CONTÉM regras de negócio, apenas orquestra a interação.


public class Main {
	
    // Pede ao AppConfig os serviços já prontos e configurados
	private static final ISessionService sessionService = AppConfig.getSessionService();
    private static final IDepartamentoService departamentoService = AppConfig.getDepartamentoService();
    private static final IFuncionarioService funcionarioService = AppConfig.getFuncionarioService();
    private static final IOcorrenciaService ocorrenciaService = AppConfig.getOcorrenciaService();

    public static void main(String[] args) {
    	//Inicia Thread que fica verificando ocorrencias vencidas.
        new Thread(new VerificadorOcorrencias(AppConfig.getOcorrenciaDAO())).start();
        Scanner scanner = new Scanner(System.in);
        System.out.println("=================================================");
        System.out.println("   Sistema de Registro de Ocorrências");
        System.out.println("=================================================");
        while (true) {
            if (!SessionManager.hasSession()) {
                handleLogin(scanner);
            } else {
                exibirMenuPrincipal(scanner);
            }
        }
    }

    private static void handleLogin(Scanner scanner) {
        try {
            System.out.print("\nLOGIN - Digite sua matrícula (ou 0 para sair): ");
            int matricula = scanner.nextInt(); scanner.nextLine();
            if (matricula == 0) { System.out.println("\nSaindo do sistema. Até logo!"); System.exit(0); }
            sessionService.login(matricula);
            System.out.println("\nLogin bem-sucedido! Bem-vindo(a), " + SessionManager.getUsuarioLogado().getNome());
        } catch (InputMismatchException e) {
            System.err.println("ERRO: Entrada inválida. Por favor, digite um número."); scanner.nextLine();
        } catch (Exception e) {
            System.err.println("ERRO de login: " + e.getMessage());
        }
    }

    private static void exibirMenuPrincipal(Scanner scanner) {
        Funcionario usuario = SessionManager.getUsuarioLogado();
        if (usuario instanceof Diretor) { menuDiretor(scanner); }
        else if (usuario instanceof Gerente) { menuGerente(scanner); }
        else { menuFuncionario(scanner); }
    }

    private static void menuDiretor(Scanner scanner) {
        System.out.println("\n--- MENU DIRETOR ---");
        System.out.println("1. Gerenciar Departamentos");
        System.out.println("2. Gerenciar Gestores (Gerentes)");
        System.out.println("3. Listar todos os Funcionários");
        System.out.println("9. Logout");
        System.out.print("Escolha uma opção: ");
        try {
            int opcao = scanner.nextInt(); scanner.nextLine();
            switch (opcao) {
                case 1: menuCRUDDepartamentos(scanner); break;
                case 2: menuCRUDGestores(scanner); break;
                case 3: handleListarTodosFuncionarios(); break;
                case 9: sessionService.logout(); System.out.println("\nLogout realizado."); break;
                default: System.out.println("Opção inválida.");
            }
        } catch (InputMismatchException e) { System.err.println("ERRO: Entrada inválida."); scanner.nextLine(); }
        pausar(scanner);
    }
    
    private static void menuCRUDDepartamentos(Scanner scanner) {
        System.out.println("\n--- Gerenciamento de Departamentos ---");
        System.out.println("1. Criar novo");
        System.out.println("2. Listar todos");
        System.out.println("3. Alterar existente");
        System.out.println("4. Excluir");
        System.out.println("9. Voltar");
        System.out.print("Escolha uma opção: ");
        try {
            int opcao = scanner.nextInt(); scanner.nextLine();
            switch (opcao) {
                case 1: handleCriarDepartamento(scanner); break;
                case 2: handleListarDepartamentos(); break;
                case 3: handleAlterarDepartamento(scanner); break;
                case 4: handleExcluirDepartamento(scanner); break;
                case 9: return;
                default: System.out.println("Opção inválida.");
            }
        } catch (InputMismatchException e) { System.err.println("ERRO: Entrada inválida."); scanner.nextLine(); }
    }
    
    private static void menuCRUDGestores(Scanner scanner) {
        System.out.println("\n--- Gerenciamento de Gestores ---");
        System.out.println("1. Cadastrar novo Gerente");
        System.out.println("2. Alterar Gerente");
        System.out.println("3. Excluir Gerente");
        System.out.println("9. Voltar");
        System.out.print("Escolha uma opção: ");
        try {
            int opcao = scanner.nextInt(); scanner.nextLine();
            switch (opcao) {
                case 1: handleCriarGerente(scanner); break;
                case 2: handleAlterarGerente(scanner); break;
                case 3: handleExcluirFuncionario(scanner); break;
                case 9: return;
                default: System.out.println("Opção inválida.");
            }
        } catch (InputMismatchException e) { System.err.println("ERRO: Entrada inválida."); scanner.nextLine(); }
    }

    private static void handleCriarGerente(Scanner scanner) {
        try {
            System.out.println("\n--- Cadastro de Novo Gerente ---");
            System.out.print("Nome do gerente: "); String nome = scanner.nextLine();
            System.out.print("Status (Ativo/Inativo): "); String status = scanner.nextLine();
            System.out.print("Código do Departamento que irá gerenciar: "); int deptoId = scanner.nextInt(); scanner.nextLine();
            Departamento depto = departamentoService.buscarPorId(deptoId);
            if (depto == null) { throw new Exception("Departamento com código " + deptoId + " não existe."); }
            Gerente novoGerente = new Gerente();
            novoGerente.setNome(nome);
            novoGerente.setStatus(status);
            novoGerente.setDepartamento(depto);
            funcionarioService.criarFuncionario(novoGerente);
            System.out.println("SUCESSO: Gerente cadastrado!");
        } catch (Exception e) { System.err.println("ERRO ao criar gerente: " + e.getMessage()); }
    }
    
    private static void handleAlterarGerente(Scanner scanner) {
        try {
            System.out.print("\nDigite a MATRÍCULA do gerente que deseja alterar: ");
            int matricula = scanner.nextInt(); scanner.nextLine();
            
            Funcionario func = funcionarioService.buscarPorMatricula(matricula);
            if (func == null || !(func instanceof Gerente)) {
                System.out.println("ERRO: Gerente com matrícula " + matricula + " não encontrado.");
                return;
            }
            System.out.println("Alterando: " + func.getNome());
            System.out.printf("Novo nome (atual: '%s' | Deixe em branco para não alterar): ", func.getNome()); String nome = scanner.nextLine();
            System.out.printf("Novo status (atual: '%s' | Deixe em branco para não alterar): ", func.getStatus()); String status = scanner.nextLine();
            System.out.printf("Novo Cód. Depto (atual: %d | Digite 0 para não alterar): ", func.getDepartamento().getCodigo()); int deptoId = scanner.nextInt(); scanner.nextLine();

            if (!nome.isBlank()) func.setNome(nome);
            if (!status.isBlank()) func.setStatus(status);
            if (deptoId != 0) {
                Departamento novoDepto = departamentoService.buscarPorId(deptoId);
                if (novoDepto == null) { throw new Exception("Departamento com código " + deptoId + " não existe."); }
                func.setDepartamento(novoDepto);
            }
            funcionarioService.alterarFuncionario(func);
            System.out.println("SUCESSO: Gerente alterado!");
        } catch (Exception e) { System.err.println("ERRO: " + e.getMessage()); }
    }

    private static void handleExcluirFuncionario(Scanner scanner) {
        try {
            System.out.print("\nDigite a MATRÍCULA do funcionário/gerente que deseja excluir: ");
            int matricula = scanner.nextInt(); scanner.nextLine();
            System.out.print("TEM CERTEZA? Esta ação não pode ser desfeita. (S/N): "); String confirmacao = scanner.nextLine();
            if ("S".equalsIgnoreCase(confirmacao)) {
                funcionarioService.deletarFuncionario(matricula);
                System.out.println("SUCESSO: Usuário excluído!");
            } else { System.out.println("Operação cancelada."); }
        } catch (Exception e) { System.err.println("ERRO: " + e.getMessage()); }
    }

    private static void handleListarTodosFuncionarios() {
        System.out.println("\n--- Lista de Todos os Funcionários ---");
        List<Funcionario> funcionarios = funcionarioService.listarTodos();
        if (funcionarios.isEmpty()) { System.out.println("Nenhum funcionário cadastrado."); } 
        else {
            System.out.printf("%-5s | %-25s | %-12s | %-20s | %s\n", "Mat.", "Nome", "Cargo", "Departamento", "Status");
            System.out.println("-----------------------------------------------------------------------------------------");
            for(Funcionario f : funcionarios) {
                String tipo = (f instanceof Diretor) ? "Diretor" : (f instanceof Gerente) ? "Gerente" : "Funcionário";
                String depto = (f.getDepartamento() != null) ? f.getDepartamento().getNome() : "N/A";
                System.out.printf("%-5d | %-25s | %-12s | %-20s | %s\n", f.getMatricula(), f.getNome(), tipo, depto, f.getStatus());
            }
        }
    }
    
    private static void handleCriarDepartamento(Scanner scanner) {
        try {
            System.out.println("\n--- Novo Departamento ---");
            System.out.print("Nome: "); String nome = scanner.nextLine();
            System.out.print("Descrição: "); String desc = scanner.nextLine();
            System.out.print("Status (Ativo/Inativo): "); String status = scanner.nextLine();
            Departamento novoDepto = new Departamento();
            novoDepto.setNome(nome); novoDepto.setDescricao(desc); novoDepto.setStatus(status);
            departamentoService.criarDepartamento(novoDepto);
            System.out.println("SUCESSO: Departamento criado!");
        } catch (Exception e) { System.err.println("ERRO: " + e.getMessage()); }
    }

    private static void handleListarDepartamentos() {
        System.out.println("\n--- Lista de Departamentos ---");
        List<Departamento> departamentos = departamentoService.listarDepartamentos();
        if (departamentos.isEmpty()) { System.out.println("Nenhum departamento cadastrado."); }
        else { departamentos.forEach(System.out::println); }
    }

    private static void handleAlterarDepartamento(Scanner scanner) {
        try {
            System.out.print("\nDigite o CÓDIGO do depto. que deseja alterar: ");
            int codigo = scanner.nextInt(); scanner.nextLine();
            Departamento depto = departamentoService.buscarPorId(codigo);
            if (depto == null) { System.out.println("ERRO: Departamento com código " + codigo + " não encontrado."); return; }
            System.out.println("Alterando: " + depto.getNome());
            System.out.printf("Novo nome (atual: '%s' | Deixe em branco para não alterar): ", depto.getNome()); String nome = scanner.nextLine();
            System.out.printf("Nova descrição (atual: '%s' | Deixe em branco para não alterar): ", depto.getDescricao()); String desc = scanner.nextLine();
            System.out.printf("Novo status (atual: '%s' | Deixe em branco para não alterar): ", depto.getStatus()); String status = scanner.nextLine();
            if (!nome.isBlank()) depto.setNome(nome);
            if (!desc.isBlank()) depto.setDescricao(desc);
            if (!status.isBlank()) depto.setStatus(status);
            departamentoService.alterarDepartamento(depto);
            System.out.println("SUCESSO: Departamento alterado!");
        } catch (Exception e) { System.err.println("ERRO: " + e.getMessage()); }
    }
    
    private static void handleExcluirDepartamento(Scanner scanner) {
        try {
            System.out.print("\nDigite o CÓDIGO do depto. que deseja excluir: ");
            int codigo = scanner.nextInt(); scanner.nextLine();
            System.out.print("TEM CERTEZA que deseja excluir o departamento Cód " + codigo + "? Esta ação não pode ser desfeita. (S/N): ");
            String confirmacao = scanner.nextLine();
            if ("S".equalsIgnoreCase(confirmacao)) {
                departamentoService.deletarDepartamento(codigo);
                System.out.println("SUCESSO: Departamento excluído!");
            } else { System.out.println("Operação cancelada."); }
        } catch (Exception e) { System.err.println("ERRO: " + e.getMessage()); }
    }

    private static void menuGerente(Scanner scanner) {
        System.out.println("\n--- MENU GERENTE ---");
        System.out.println("1. Gerenciar Ocorrências do meu Depto.");
        System.out.println("2. Gerenciar Funcionários do meu Depto.");
        System.out.println("9. Logout");
        System.out.print("Escolha uma opção: ");
        try {
            int opcao = scanner.nextInt(); scanner.nextLine();
            switch (opcao) {
                case 1: menuGerenteOcorrencias(scanner); break;
                case 2: menuCRUDFuncionarios(scanner); break;
                case 9: sessionService.logout(); System.out.println("\nLogout realizado."); break;
                default: System.out.println("Opção inválida.");
            }
        } catch (InputMismatchException e) { System.err.println("ERRO: Entrada inválida."); scanner.nextLine(); }
        pausar(scanner);
    }
    
    private static void menuGerenteOcorrencias(Scanner scanner) {
        System.out.println("\n--- Gerenciamento de Ocorrências (Gerente) ---");
        System.out.println("1. Listar Ocorrências do meu Depto.");
        System.out.println("2. Registrar Nova Ocorrência");
        System.out.println("3. Fechar Ocorrência (Definitivo)");
        System.out.println("9. Voltar");
        System.out.print("Escolha uma opção: ");
        try {
            int opcao = scanner.nextInt(); scanner.nextLine();
            switch (opcao) {
                case 1: handleListarOcorrenciasDoDepartamento(); break;
                case 2: handleRegistrarOcorrencia(scanner); break;
                case 3: handleFecharOcorrenciaDefinitivo(scanner); break;
                case 9: return;
                default: System.out.println("Opção inválida.");
            }
        } catch (InputMismatchException e) { System.err.println("ERRO: Entrada inválida."); scanner.nextLine(); }
    }

    private static void handleListarOcorrenciasDoDepartamento() {
        System.out.println("\n--- Ocorrências do seu Departamento ---");
        try {
            List<Ocorrencia> ocorrencias = ocorrenciaService.consultarOcorrenciasDoMeuDepartamento();
            if (ocorrencias.isEmpty()) {
                System.out.println("Nenhuma ocorrência encontrada para o seu departamento.");
            } else {
                for (Ocorrencia o : ocorrencias) {
                    System.out.println("------------------------------------");
                    System.out.println(o);
                }
                System.out.println("------------------------------------");
            }
        } catch (Exception e) {
            System.err.println("ERRO: " + e.getMessage());
        }
    }

    private static void menuCRUDFuncionarios(Scanner scanner) {
        System.out.println("\n--- Gerenciamento de Funcionários ---");
        System.out.println("1. Cadastrar novo Funcionário");
        System.out.println("2. Listar Funcionários do meu Depto.");
        System.out.println("3. Alterar Funcionário");
        System.out.println("4. Excluir Funcionário");
        System.out.println("9. Voltar");
        System.out.print("Escolha uma opção: ");
        try {
            int opcao = scanner.nextInt(); scanner.nextLine();
            switch (opcao) {
                case 1: handleCriarFuncionarioComum(scanner); break;
                case 2: handleListarFuncionariosDoDepartamento(); break;
                case 3: handleAlterarFuncionarioComum(scanner); break;
                case 4: handleExcluirFuncionario(scanner); break; // Reutiliza o handler do diretor
                case 9: return;
                default: System.out.println("Opção inválida.");
            }
        } catch (InputMismatchException e) { System.err.println("ERRO: Entrada inválida."); scanner.nextLine(); }
    }
    
    private static void handleCriarFuncionarioComum(Scanner scanner) {
        try {
            System.out.println("\n--- Cadastro de Novo Funcionário ---");
            System.out.print("Nome do funcionário: "); String nome = scanner.nextLine();
            System.out.print("Status (Ativo/Inativo): "); String status = scanner.nextLine();

            // Departamento é definido automaticamente como o do gerente logado
            Departamento deptoDoGerente = SessionManager.getUsuarioLogado().getDepartamento();
            System.out.println("Departamento: " + deptoDoGerente.getNome());

            Funcionario novoFuncionario = new Funcionario();
            novoFuncionario.setNome(nome);
            novoFuncionario.setStatus(status);
            novoFuncionario.setDepartamento(deptoDoGerente);
            
            funcionarioService.criarFuncionario(novoFuncionario);
            System.out.println("SUCESSO: Funcionário cadastrado!");
        } catch (Exception e) { System.err.println("ERRO: " + e.getMessage()); }
    }

    private static void handleListarFuncionariosDoDepartamento() {
        System.out.println("\n--- Funcionários do seu Departamento ---");
        try {
            List<Funcionario> funcionarios = funcionarioService.listarFuncionariosDoMeuDepartamento();
            if(funcionarios.isEmpty()) { System.out.println("Nenhum funcionário encontrado em seu departamento."); }
            else {
                for(Funcionario f : funcionarios) {
                    System.out.printf("Mat: %-5d | Nome: %-25s | Status: %s\n", f.getMatricula(), f.getNome(), f.getStatus());
                }
            }
        } catch (Exception e) { System.err.println("ERRO: " + e.getMessage()); }
    }

    private static void handleAlterarFuncionarioComum(Scanner scanner) {
        try {
            System.out.print("\nDigite a MATRÍCULA do funcionário que deseja alterar: ");
            int matricula = scanner.nextInt(); scanner.nextLine();
            
            Funcionario func = funcionarioService.buscarPorMatricula(matricula);
            if (func == null || func instanceof Gerente || func instanceof Diretor) {
                System.out.println("ERRO: Funcionário comum com matrícula " + matricula + " não encontrado ou é um gestor.");
                return;
            }

            System.out.println("Alterando: " + func.getNome());
            System.out.printf("Novo nome (atual: '%s' | Deixe em branco para não alterar): ", func.getNome()); String nome = scanner.nextLine();
            System.out.printf("Novo status (atual: '%s' | Deixe em branco para não alterar): ", func.getStatus()); String status = scanner.nextLine();

            if (!nome.isBlank()) func.setNome(nome);
            if (!status.isBlank()) func.setStatus(status);

            funcionarioService.alterarFuncionario(func);
            System.out.println("SUCESSO: Funcionário alterado!");
        } catch (Exception e) { System.err.println("ERRO: " + e.getMessage()); }
    }

    private static void handleRegistrarOcorrencia(Scanner scanner) {
        try {
            System.out.println("\n--- Nova Ocorrência ---");
            System.out.print("Descrição da ocorrência: "); String desc = scanner.nextLine();
            System.out.print("Data da ocorrência (AAAA-MM-DD): "); LocalDate dataOcorrencia = LocalDate.parse(scanner.nextLine());
            System.out.print("Data limite (AAAA-MM-DD): "); LocalDate dataLimite = LocalDate.parse(scanner.nextLine());
            System.out.print("Matrícula do funcionário de TI a ser alocado: "); int matFunc = scanner.nextInt(); scanner.nextLine();
            Ocorrencia novaOcorrencia = new Ocorrencia();
            novaOcorrencia.setDescricao(desc);
            novaOcorrencia.setDataOcorrencia(dataOcorrencia);
            novaOcorrencia.setDataLimiteSolucao(dataLimite);
            novaOcorrencia.setDepartamentoReportante(SessionManager.getUsuarioLogado().getDepartamento());
            Funcionario funcAlocado = new Funcionario();
            funcAlocado.setMatricula(matFunc);
            novaOcorrencia.setFuncionarioAlocado(funcAlocado);
            ocorrenciaService.registrarOcorrencia(novaOcorrencia);
            System.out.println("SUCESSO: Ocorrência registrada!");
        } catch (DateTimeParseException e) { System.err.println("ERRO: Formato de data inválido. Use AAAA-MM-DD.");
        } catch (Exception e) { System.err.println("ERRO: " + e.getMessage()); }
    }

    private static void handleFecharOcorrenciaDefinitivo(Scanner scanner) {
        try {
            System.out.print("Digite o número da ocorrência para fechar definitivamente: ");
            int numero = scanner.nextInt(); scanner.nextLine();
            ocorrenciaService.fecharOcorrenciaDefinitivamente(numero);
            System.out.println("SUCESSO: Ocorrência N°" + numero + " fechada definitivamente.");
        } catch (Exception e) { System.err.println("ERRO: " + e.getMessage()); }
    }

    private static void menuFuncionario(Scanner scanner) {
        System.out.println("\n--- MENU FUNCIONÁRIO ---");
        System.out.println("1. Listar minhas Ocorrências alocadas");
        System.out.println("2. Encerrar Ocorrência (Temporário)");
        System.out.println("9. Logout");
        System.out.print("Escolha uma opção: ");
        try {
            int opcao = scanner.nextInt(); scanner.nextLine();
            switch (opcao) {
                case 1: handleListarMinhasOcorrencias(); break;
                case 2: handleEncerrarOcorrenciaTemporario(scanner); break;
                case 9: sessionService.logout(); System.out.println("\nLogout realizado."); break;
                default: System.out.println("Opção inválida.");
            }
        } catch (InputMismatchException e) { System.err.println("ERRO: Entrada inválida."); scanner.nextLine(); }
        pausar(scanner);
    }
    
    private static void handleListarMinhasOcorrencias() {
        System.out.println("\n--- Minhas Ocorrências Alocadas ---");
        try {
            List<Ocorrencia> ocorrencias = ocorrenciaService.consultarMinhasOcorrencias();
            if (ocorrencias.isEmpty()) { System.out.println("Nenhuma ocorrência alocada para você no momento."); } 
            else {
                for (Ocorrencia o : ocorrencias) {
                    System.out.println("------------------------------------");
                    System.out.println(o);
                }
                System.out.println("------------------------------------");
            }
        } catch (AuthException e) { System.err.println("Permissão negada: " + e.getMessage()); }
    }
    
    private static void handleEncerrarOcorrenciaTemporario(Scanner scanner) {
        try {
            System.out.print("Digite o número da ocorrência para encerrar temporariamente: ");
            int numero = scanner.nextInt(); scanner.nextLine();
            ocorrenciaService.encerrarOcorrenciaTemporariamente(numero);
            System.out.println("SUCESSO: Ocorrência N°" + numero + " marcada como encerrada temporariamente.");
        } catch (Exception e) { System.err.println("ERRO: " + e.getMessage()); }
    }

    private static void pausar(Scanner scanner) {
        System.out.print("\nPressione Enter para continuar...");
        scanner.nextLine();
    }
}
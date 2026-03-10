-- Schema mínimo para uso do sistema de registro de ocorrências
-- Contém somente DDL e os inserts mínimos necessários para iniciar o sistema.

CREATE DATABASE IF NOT EXISTS `registro_ocorrencias` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `registro_ocorrencias`;

SET FOREIGN_KEY_CHECKS = 0;

-- Tabelas (criação mínima)
DROP TABLE IF EXISTS `ocorrencias`;
DROP TABLE IF EXISTS `funcionarios`;
DROP TABLE IF EXISTS `departamentos`;

CREATE TABLE `departamentos` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `descricao` text,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`codigo`),
  UNIQUE KEY `nome` (`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `funcionarios` (
  `matricula` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `id_departamento` int DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `tipo_funcionario` enum('COMUM','GERENTE','DIRETOR') NOT NULL,
  PRIMARY KEY (`matricula`),
  KEY `id_departamento` (`id_departamento`),
  CONSTRAINT `funcionarios_ibfk_1` FOREIGN KEY (`id_departamento`) REFERENCES `departamentos` (`codigo`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ocorrencias` (
  `numero` int NOT NULL AUTO_INCREMENT,
  `descricao` text NOT NULL,
  `data_ocorrencia` date NOT NULL,
  `id_departamento_reportante` int NOT NULL,
  `matricula_funcionario_alocado` int NOT NULL,
  `data_limite_solucao` date NOT NULL,
  `status_temporario` varchar(20) NOT NULL,
  `status_definitivo` varchar(20) NOT NULL,
  PRIMARY KEY (`numero`),
  KEY `id_departamento_reportante` (`id_departamento_reportante`),
  KEY `matricula_funcionario_alocado` (`matricula_funcionario_alocado`),
  CONSTRAINT `ocorrencias_ibfk_1` FOREIGN KEY (`id_departamento_reportante`) REFERENCES `departamentos` (`codigo`),
  CONSTRAINT `ocorrencias_ibfk_2` FOREIGN KEY (`matricula_funcionario_alocado`) REFERENCES `funcionarios` (`matricula`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

-- Inserts mínimos necessários para usar o sistema
-- 1) departamento Informática (nome importante, usado nas validações)
-- 2) um departamento adicional (ex.: Comercial) para ser gerenciado
-- 3) um Diretor (para operações administrativas)
-- 4) um Gerente no departamento 2 (para abrir/fechar ocorrências)
-- 5) um Técnico em Informática (COMUM) para ser alocado em ocorrências

-- Limpa dados antigos (opcional) e insere mínimos
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE ocorrencias;
TRUNCATE TABLE funcionarios;
TRUNCATE TABLE departamentos;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO departamentos (codigo, nome, descricao, status) VALUES
(1, 'Informática', 'Departamento de Tecnologia da Informação', 'Ativo'),
(2, 'Comercial', 'Departamento Comercial', 'Ativo');

-- Ajusta auto_increment para manter sequência limpa
ALTER TABLE departamentos AUTO_INCREMENT = 3;

INSERT INTO funcionarios (matricula, nome, id_departamento, status, tipo_funcionario) VALUES
(1, 'Diretor Inicial', NULL, 'Ativo', 'DIRETOR'),
(100, 'Gerente Comercial', 2, 'Ativo', 'GERENTE'),
(200, 'Técnico TI', 1, 'Ativo', 'COMUM');

ALTER TABLE funcionarios AUTO_INCREMENT = 201;

-- Nenhuma ocorrência inicial é estritamente necessária; crie via interface quando desejar.

-- Fim do schema mínimo

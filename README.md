# Rubi

Projeto de rede social feito por Bianca Almeida de Oliveira Bezerra e Ruan Macedo Santos

## Uso

As dependências do projeto são gerenciadas pelo Maven, então tenha certeza de que ele está instalado antes de tentar executar o código.

No Ubuntu e derivados, você pode obter o Maven via terminal assim:

```bash
  sudo apt install maven
```

## Documentação

Inicialmente os atributos privados estavam sendo nomeados com o padrão Python (__atribute_), mas após utilizar a biblioteca Lombok para gerar os getters automaticamente, essa nomenclatura se tornou inviável, pois atributos como __id,_ tinham métodos _get_id()_, enquanto esperava-se um _getId()_.

## Banco De Dados

### + Arquivos

Na persistência com arquivos de texto, os daddos são salvos no formato abaixo, separados por ponto e vírgula(;).

#### _PERFIL_

|    IdPerfil    |    Username   |        Nome    |        Email        |     Biografia    |
| :--------------: | :-------------: | :--------------: | :-------------------: | :-------------: |
| 140a3dd2-2c... |   bbianca     | Bianca Bezerra | bianca-bz@gmail.com | I'm Bia |
| 4ffd42c0-33... |   msruan      | Ruan Macedo    | ruan.macs@gmail.com | I'm Ruan |

#### _POSTAGEM_

| Tipo |    IdPost      |    IdPerfil    |        Data          | Texto     | Likes | Deslikes | ViewsRestantes | Hashtags<> |
| :----: | :--------------: | :--------------: | :--------------------: | :---------: | :-----: | :--------: | :---------------: | :-----------: |
|   1  | 3648f487-20... | 140a3dd2-2c... | 2023-11-01T09:39:33Z | love you! |  15   |    3     |      50         | #love#feel  |
|   0  | b70566f9-62... | 4ffd42c0-33... | 2021-05-28T21:00:45Z | quer ler? |  12   |    2     |                 |             |

_**0 = Postagem**_, _**1 = PostagemAvancada**_.

### + SQL

Utilizamos MySQL para conexão com o BD. Antes, é necessário criar o BD, cujo script de criação está nos arquivos do projeto no foramto .sql.
Única diferença para as tabelas do modelo de arquivos, é que não há coluna para guardar o tipo de Postagem.

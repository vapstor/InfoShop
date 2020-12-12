<h1 align="center">
  InfoShop
</h1>

<p align="center">
  <strong>Repositório para centralizar o Ambiente de Desenvolvimento</strong>
  <p align="center">
    <img src="https://ci.appveyor.com/api/projects/status/g8d58ipi3auqdtrk/branch/master?svg=true" alt="Config. Device Activity Passing." />
<!--      <img src="https://ci.appveyor.com/api/projects/status/216h1g17b8ir009t?svg=true" alt="Config. Device Activity Crashing." /> -->
    <img src="https://img.shields.io/badge/version-1.0.0-blue.svg" alt="Current APP version." />  
  </p>
</p>

## 📋 Briefing

  Aplicativo (MVP) para listar projetos de diferentes profissionais (Elétrica, Mecânica, Civil, Arquitetura, etc...). 
  Desenvolvido em java utilizando a arquitetura MVVM e firebase como back-end e armazenamento de dados.

## 📖 Requirements
```
    📱 minSdkVersion 23
    📱 targetSdkVersion 29

    implementation fileTree(dir: "libs", include: ["*.jar"])
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.1'
    implementation 'androidx.transition:transition:1.3.1'
    implementation 'androidx.annotation:annotation:1.1.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-savedstate:2.3.0-alpha07'
    implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0'
    implementation 'androidx.navigation:navigation-fragment:2.3.0'
    implementation 'androidx.navigation:navigation-ui:2.3.0'

    implementation 'com.google.android.material:material:1.3.0-alpha02'
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
    implementation 'com.squareup.picasso:picasso:2.71828'

    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test.ext:junit:1.1.2'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'
```

## 🚀 ScreensShots
<div style="float: left">
</div>

## 👏 Todo (Desenvolvimento)

- [x] Criar repositório no Github
- [X] Utilizar MVVM Architeture
- [x] Utilizar Fundamentos do Material Design
- [x] Criar SplashScreen
- [x] Criar LoginPage
- [x] Conexão com o Firebase
- [x] Configurar botões de BottomNavigation:
  - [x] Perfil
  - [x] Home
  - [x] Projetos
- [x] Configurar pesquisa na aba Projetos
- [x] Permitir salvar/excluir projetos favoritos

* Desenvolver LoginPage ["LOGIN"]

  - [x] Implementar TextInputsLayouts e TextViews
  - [x] Implementar autenticação no firebase
  
* Desenvolver CadastroPage ["Cadastro"]
  - [x] Implementar Inputs (nome, usuário, email, senha, telefone (Máscara), Endereço
  - [x] Implementar Button "cadastrar"
  - [x] Implementar cadastro no firebase
  
* Desenvolver HomePage ["HOME"]
  -  [x] Implementar views de boas-vindas dinâmica
  -  [x] Implementar Bottom Navigation
  -  [x] Implementar Layout do item projeto
  -  [x] Implementar EndlessScrollView para lista de projetos
  -  [x] Implementar SwipeRefresh para a lista de projetos
  -  [x] Implementar detalhamento ao clicar no Produto
  -  [x] Implementar Swipe para excluir um item indesejável da lista
  -  [x] Implementar Ordenação por "Preço" na lista de projetos
  
* Desenvolver ProfilePage ["Perfil"]
  - [x] Implementar Views (Nome, Email, Endereço)
  - [x] Implementar Button para fragmento favoritos
  
  * Desenvolver FavoritosFragment
    - [x] Implementar RecyclerView com lista de projetos favoritos
    - [x] Implementar ImageButton (star) para desfavoritar e assim remove-lo da lista

* Desenvolver ProjetosPage ["Projetos"]
  * Desenvolver CategoriasFragment
    - [x] Implementar gridview (4) categorias
    - [x] Implementar GET firebase 
  * Desenvolver ProjetosFragment
    - [x] Implementar SearchBar ("Busque um projeto...")
    - [x] Implementar pesquisa por nome/descrição de projeto (regex)
    - [x] Implementar botão para limpar seção de pesquisa
    - [x] Implementar Ordenação (A definir)
    
## How to version

Versionamento será dividido entre

- Mudanças significativas de funcionalidade do App (+x.0.0)
- Adição de novas funcionalidades (0.+x.0)
- Ajustes de Bugs (0.0.+x)

#### Exemplo:

> Foram adicionadas 3 novas telas, 5 novas funcionalidades e corrigidos 15 bugs. Logo a versão continuará 1, porém com 8 incrementos de funcionalidades e 15 correções de bugs. Versão final: 1.8.15

#### 👏 Todo (README.MD)

- [ ] Implementar ScreensShots no README.MD
- [x] Adicionar Dependências
- [x] Incrementar Todo(Dev)


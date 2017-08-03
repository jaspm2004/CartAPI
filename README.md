# CartAPI

API de compras responsável por controlar um carrinho de compras e finalização da compra. 
Oferece uma interface REST para inserção de produtos no carrinho e checkout da compra. 
O checkout da compra afeta o estoque de produtos.

### Tecnologias utilizadas

* Maven
* Spring Boot
* H2

### Para rodar este projeto localmente
```
$ git clone https://github.com/jaspm2004/CartAPI
$ mvn clean install
$ mvn dependency:copy-dependencies
$ cd target
$ java -jar .\CartAPI-1.0.0.jar
```
A API REST fica em http://localhost:7070/cartapi/carts

A referência para a API de produtos fica no application.yml
```
productapi:
    host: localhost
    port: 8080
    app: productapi
```

### Para importar este projeto utilizando IDE

* Fazer o clone: https://github.com/jaspm2004/CartAPI
* Clean & Build
* Run

### Exemplos de uso
* Para cadastrar um novo produto
```
fazer um POST em http://localhost:8080/productapi/products
passar parâmetros no Request Body: name = <string> e stock = <int>
```
* Para pesquisar um produto
```
fazer um GET em http://localhost:8080/productapi/products (lista todos os produtos)
fazer um GET em http://localhost:8080/productapi/products/<id> (filtra pelo id)
fazer um GET em http://localhost:8080/productapi/products?name=<nome do produto> (filtra pelo nome)
```
* Para modificar stock de um produto
```
fazer um PATCH em http://localhost:8080/productapi/products/<id>/stock/<qtd>
qtd > 0 para acrescentar 
qtd < 0 para decrementar
```



<?php

include 'connection.php' ;


// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();

//SI LA PETICION ENTRANTE ES UN POST
if($_SERVER['REQUEST_METHOD'] == 'POST'){

    $sql = "insert into tblclient (name, email, pass) values(:name, :email, :pass)";
    //MEDIANTE PREPARE() SE CREA UNA INSTANCIA DE LA CLASE PDO STATEMENT
    //PARA PASAR LOS VALORES O EJECUTAR SENTENCIAS
    $stmt = $pdo->prepare($sql);
    
    //CON BINDVALUE SE ENLAZA EL VALOR DE LA VARIABLE
    $stmt->bindValue(':name',$_POST['name']);
    $stmt->bindValue(':email',$_POST['email']);
    $stmt->bindValue(':pass',$_POST['pass']);
    //EJECUTA LA SENTENCIA SQL, ENVIA LOS DATOS A LA BASE
    $stmt->execute();
}





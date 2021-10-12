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


    $name="Marcelo";
    $email="m@gmail.com";
    $pass="123456";
    
    //CON BINDVALUE SE ENLAZA EL VALOR DE LA VARIABLE
    $stmt->bindValue(':name',$name);
    $stmt->bindValue(':email',$email);
    $stmt->bindValue(':pass',$pass);
    //EJECUTA LA SENTENCIA SQL, ENVIA LOS DATOS A LA BASE
    $stmt->execute();
    /* $idPost = $pdo->lastInsertId();
    if($idPost){
        $val = array("id" => $idPost);
        echo json_encode($val);
    } */

   /*  $versiones = array(
        "versiones_android" => array(
            array(
                "froyo" => "2.2"
            ),
            array(
                "gingerbread" => "2.3"
            )
        )
    ); */

    $versiones = array("estado"=>"201");
    header('Content-Type: application/json');
    echo json_encode($versiones);
    
}





<?php

$hostname = "localhost";
$username = "root";
$password = "1998*";
$database_name = "dbreturnhome";

$contenido = file_get_contents("php://input");

if(isset($contenido)){

    $data = json_decode($contenido, true);

    //CONEXION A LA BASE DE DATOS
    $conexion=new PDO("mysql:host=".$hostname.";dbname=".$database_name."",
                                    $username,
                                    $password,
                                    array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

    $stmt = $conexion->prepare("UPDATE  tblcliente SET nombre = ?,email = ?,numeroCelular = ? WHERE idCliente = ?");
    $stmt->bindValue(1,$data["nombre"]);
    $stmt->bindValue(2,$data["email"]);
    $stmt->bindValue(3,$data["numeroCelular"]);
    $stmt->bindValue(4,$data["idCliente"]);

    if($stmt->execute()){
        $stmt=null;
        http_response_code(200);
    }
    else{
        http_response_code(304);
    }
}
else{
    http_response_code(304);
}

?>
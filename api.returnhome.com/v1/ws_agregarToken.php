<?php

//CREDENCIALES DE ACCESO A LA BASE DE DATOS
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

    $consulta = "INSERT INTO tbltoken (token, idCliente) values(?, ?)";

    $stmt = $conexion->prepare($consulta);
    $stmt->bindValue(1,$data["token"]);
    $stmt->bindValue(2,$data["idCliente"]);
    $stmt->execute();

    $fila_agregada=$stmt->rowCount();
    $stmt=null;

    if($fila_agregada==1){
        
        http_response_code(201);
    }
    else{
        http_response_code(401);
    }
}
else{
    http_response_code(401);
}
?>
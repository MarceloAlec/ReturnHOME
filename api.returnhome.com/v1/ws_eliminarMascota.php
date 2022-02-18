<?php
//CREDENCIALES DE ACCESO A LA BASE DE DATOS
$hostname = "localhost";
$username = "root";
$password = "1998*";
$database_name = "dbreturnhome";

if(isset($_GET["idMascota"])){

    $idMascota = $_GET["idMascota"];

    //CONEXION A LA BASE DE DATOS
    $conexion=new PDO("mysql:host=".$hostname.";dbname=".$database_name."",
                                     $username,
                                     $password,
                                     array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

    $consulta = "DELETE FROM tblmascota WHERE idMascota = ?";
    $stmt = $conexion->prepare($consulta);
    $stmt->bindValue(1,$idMascota);
    $stmt->execute();

    $filas_modificadas = $stmt->rowCount();
    $stmt=null;

    if($filas_modificadas==1){
        http_response_code(200);
    }
    else{
        http_response_code(404);
    }
}
else{
    http_response_code(404);
}
?>
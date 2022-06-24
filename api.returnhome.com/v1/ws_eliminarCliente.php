<?php
$hostname = "localhost";
$username = "root";
$password = "1998*";
$database_name = "dbreturnhome";

if(isset($_GET["idCliente"])){

    $idCliente = $_GET["idCliente"]; 

    //CONEXION A LA BASE DE DATOS
    $conexion=new PDO("mysql:host=".$hostname.";dbname=".$database_name."",
                                    $username,
                                    $password,
                                    array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

    $stmt = $conexion->prepare("DELETE FROM tblcliente WHERE idCliente = ?");
    $stmt->bindValue(1,$idCliente);

    if($stmt->execute()){
        $stmt=null;
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
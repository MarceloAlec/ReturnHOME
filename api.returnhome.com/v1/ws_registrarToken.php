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
                                    
    $stmt = $conexion->prepare("INSERT INTO tbltoken (token, idCliente) values(?, ?)");
    $stmt->bindValue(1,$data["token"]);
    $stmt->bindValue(2,$data["idCliente"]);

    if($stmt->execute()){
        $stmt=null;
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
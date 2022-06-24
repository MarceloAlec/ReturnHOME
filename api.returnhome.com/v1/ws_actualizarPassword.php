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
        
    $stmt = $conexion->prepare("SELECT password FROM tblcliente WHERE idCliente = ?");
    $stmt->bindValue(1,$data["idCliente"]);

    if($stmt->execute()){
        $result = $stmt->fetch(PDO::FETCH_ASSOC);
        $stmt=null;
        
        if (password_verify($data["actualPassword"], $result["password"])) {
            $stmt = $conexion->prepare("UPDATE tblCliente SET password = ? WHERE idCliente = ?");
            $stmt->bindValue(1,password_hash($data["nuevoPassword"],PASSWORD_DEFAULT));
            $stmt->bindValue(2,$data["idCliente"]);

            if($stmt->execute()){
                $stmt = null;
                http_response_code(200);
            }
            else{
                http_response_code(304);
            }          
        }
        else{
            http_response_code(304);
        } 
    }
    else{
        http_response_code(304);
    }
}
else{
    http_response_code(304);
}
?>
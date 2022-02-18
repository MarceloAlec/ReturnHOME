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

        
    $consulta = "SELECT password FROM tblcliente WHERE idCliente = ?";
        
    $stmt = $conexion->prepare($consulta);
    $stmt->bindValue(1,$data["idCliente"]);
    $stmt->execute();

    $num = $stmt->rowCount();

    if($num==1){
        $result = $stmt->fetch(PDO::FETCH_ASSOC);

        $stmt=null;
        
        if (password_verify($data["actualPassword"], $result["password"])) {
            $consulta = "UPDATE tblCliente SET password = ? WHERE idCliente = ?";
            $stmt = $conexion->prepare($consulta);
            $stmt->bindValue(1,password_hash($data["nuevoPassword"],PASSWORD_DEFAULT));
            $stmt->bindValue(2,$data["idCliente"]);
            $stmt->execute();
            $fila_modificada = $stmt->rowCount();

            if($fila_modificada==1){
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
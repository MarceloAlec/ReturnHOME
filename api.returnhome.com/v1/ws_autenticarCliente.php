<?php
$hostname = "localhost";
$username = "root";
$password = "1998*";
$database_name = "dbreturnhome";

//PHP://INPUT PERMITE LEER DATOS DE UN CUERPO SOLICITADO
$contenido = file_get_contents('php://input');

if(isset($contenido)){

    $data = json_decode($contenido, true);
        
    //CONEXION A LA BASE DE DATOS
    $conexion=new PDO("mysql:host=".$hostname.";dbname=".$database_name."",
                                    $username,
                                    $password,
                                    array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

    $stmt = $conexion->prepare("SELECT * FROM tblcliente WHERE email = ?");
    $stmt->bindValue(1,$data["email"]);

    if($stmt->execute()){
        $respuesta = $stmt->fetch(PDO::FETCH_ASSOC);
        $stmt=null;

        if (password_verify($data["password"], $respuesta["password"])) {
            http_response_code(200);
            echo json_encode(array("cliente" => $respuesta));
        }
        else{
            http_response_code(404);
        }
    }
    else{  
        http_response_code(404);
    }  
}
else{
    http_response_code(404);

}
          
?>
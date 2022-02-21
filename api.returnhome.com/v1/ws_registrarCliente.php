<?php
header("Content-Type: application/json");
/*CREDENCIALES PARA ACCEDER A LA BASE DE DATOS */
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

    $consulta = "SELECT * FROM tblcliente WHERE email = ?";

    $stmt = $conexion->prepare($consulta);
    $stmt->bindValue(1,$data["email"]);
    $stmt->execute();

    $num = $stmt->rowCount();

    $stmt = null;

    if($num == 0){
        $consulta = "INSERT INTO tblcliente (nombre, email, password, numeroCelular) values(?, ?, ?, ?)";

        $stmt = $conexion->prepare($consulta);
        $stmt->bindValue(1,$data["nombre"]);
        $stmt->bindValue(2,$data["email"]);
        $stmt->bindValue(3,password_hash($data["password"],PASSWORD_DEFAULT));
        $stmt->bindValue(4,$data["numeroCelular"]);

        $stmt->execute();
        $fila_agregada = $stmt->rowCount();

        if($fila_agregada == 1){
            $stmt = null;
            $idCliente=$conexion->lastInsertId();
            http_response_code(201);
            echo json_encode(array("cliente" => array("id"=>$idCliente)));
        }
        else{
            http_response_code(401);
        }
    }
    else{
        http_response_code(401);
    } 
}
else{
    http_response_code(401);
}
 
?>




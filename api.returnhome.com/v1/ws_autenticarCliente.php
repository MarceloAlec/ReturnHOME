<?php

/*CREDENCIALES DE ACCESO A LA BASE DE DATOS */
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

    if($num>0){
        $resultado = $stmt->fetch(PDO::FETCH_ASSOC);

        $stmt=null;

        if (password_verify($data["password"], $resultado["password"])) {
            http_response_code(200);
            $cliente_auth = array("idCliente"=>$resultado["idCliente"],
                                  "nombre"=>$resultado["nombre"],
                                  "email"=>$resultado["email"],
                                  "numeroCelular"=>$resultado["numeroCelular"]);

            echo json_encode(array("cliente" => $cliente_auth));
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
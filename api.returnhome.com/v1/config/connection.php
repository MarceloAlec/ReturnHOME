<?php

class Connection extends PDO
{
    /*CREDENCIALES DE ACCESO A LA BASE DE DATOS */
    private $hostname = "localhost";
    private $username = "root";
    private $password = "1998*";
    private $database_name = "dbreturnhome";

    public function __construct(){
        try
        {
            /* EL CONSTRUCTOR DE PDO RECIBE LAS CREDENCIALES PARA LA 
            CONEXION DE LA BASE DE DATOS */
            parent::__construct("mysql:host=" . $this->hostname . ";dbname=" . $this->database_name . ";charset=utf8", $this->username, $this->password ,array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));
                        
        }catch(PDOException $ex){
            echo "Connection failed: " . $ex->getMessage();
            exit;
        }
    }


}

?>
import sun.plugin2.message.Conversation;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurMT extends Thread{
    int nbClients;
    private int nombreSecret;
    private boolean fin;
    private String gagnant;
    public void run(){
        try {
          ServerSocket ss = new ServerSocket(234);
          nombreSecret = (int)(Math.random()*1000);
            // notre serveur est capable de connecté des client a n'importe quel moment
            while (true){
                // generation dune socket
                Socket s = ss.accept();
                ++nbClients;
                // pour chaque client connecte ont generent un thread
                new Conversation(s, nbClients).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // pour chaque objet qui se connecte on cree un objet de la classe Conversation ( qui est un nouveau thread )
    // le thread cree va executé la methode run
    class Conversation extends Thread{
        private Socket socket;
        private int numeroClient;

        public Conversation(Socket socket, int num){
            super();
            this.socket = socket;
            this.numeroClient = num;
        }
        // code executer d'une maniere parallele
        // code de la conversation
        public void run(){
            //des la connexion du client ce code est execute
            try {

                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os, true);

                //les detailles du client ( @ip )
                String IP = socket.getRemoteSocketAddress().toString();
                System.out.println("Connextion du client : " + numeroClient + " avec l'adresseIP :" + IP);
                pw.println("Bienvenue , vous etes le client numero " + numeroClient);
                pw.println("Deviner le nombre secret entre 0 et 1000 ");
                while (true){
                    String req;
                    while ((req = br.readLine())!= null){
                        int nb = Integer.parseInt(req);
                        if (fin ==false){
                            if (nb<nombreSecret){
                                pw.println("voter nombre est plus petit");
                            }
                            else if (nb>nombreSecret){
                                pw.println("voter nombre est plus grand");
                            }
                            else {
                                gagnant = IP;
                                fin = true;
                                pw.println("Bravo!!! vous avez gagner");
                                System.out.println("*********");
                                System.out.println("Bravooooo :" + IP);
                                System.out.println("**********");
                            }
                        }
                        else {
                            pw.println("Le jeu est terminer, le gagant est : " + gagnant);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){

        new ServeurMT().start();
    }

}

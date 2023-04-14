import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
public class Driver extends Application {
    public void start(Stage primaryStage) {
        BorderPane bPane = new BorderPane();
        bPane.setStyle("-fx-background-color: #1A1919");
        Label statusL = new Label();
        statusL.setTextFill(Color.WHITE);
        VBox vbox = new VBox(10);
        statusL.setMinSize(265, 40);
        statusL.setFont(new Font("San Francisco", 19));
        Button compB = new Button("Compress");
        Button decompB = new Button("Extract");
        HBox hbox = new HBox(20);
        hbox.getChildren().addAll(compB, decompB);
        Scene scene = new Scene(bPane, 500, 200);
        vbox.getChildren().addAll(hbox, statusL);
        primaryStage.setScene(scene);
        primaryStage.show();
        bPane.setCenter(vbox);
        vbox.setAlignment(Pos.CENTER);
        hbox.setAlignment(Pos.CENTER);
        compB.setMinSize(200, 40);
        compB.setFont(new Font("San Francisco", 19));
        decompB.setMinSize(200, 40);
        decompB.setFont(new Font("San Francisco", 19));
        // =====================================================================
        compB.setOnAction(ie -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select A file to compress");
            File inputFile = fileChooser.showOpenDialog(primaryStage);
            Boolean result = compress(inputFile);
            if (result) {
                statusL.setText("file was compressed successfully");
            } else {
                statusL.setText("compression failed: file is empty");
            }

        });
        decompB.setOnAction(ie -> {
            FileChooser fileChooser = new FileChooser();
            ExtensionFilter extFilter = new ExtensionFilter("Compressed files (*.compressed)", "*.compressed");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setTitle("Select A file to extract");
            File inputFile = fileChooser.showOpenDialog(primaryStage);
            Boolean result = decompress(inputFile);
            if (result) {
                statusL.setText("file was extracted successfully");
            } else {
                statusL.setText("decompression failed: file is empty");
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
    public static Boolean compress(File inputFile) {
        int[] data = new int[256];
        try (InputStream inputStream = new FileInputStream(inputFile);) {
            byte[] buffer = new byte[4096];
            int bytesRead = inputStream.read(buffer);
            while ((bytesRead) != -1) {
                for (int i = 0; i < buffer.length; i++) {
                    data[Byte.toUnsignedInt(buffer[i])]++;
                }
                bytesRead = inputStream.read(buffer);
            }
            inputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        Heap temp = new Heap(data);
        String[] codes = temp.getResult();
        String dest = inputFile.getAbsolutePath();
        if (dest.contains(".")) {
            dest = dest.substring(0, dest.lastIndexOf("."));
            dest += ".compressed";
        }
        File outputFile = new File(dest);
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = new FileInputStream(inputFile);
                OutputStream outputStream = new FileOutputStream(outputFile);) {
            byte[] buffer = new byte[4096];
            byte[] bytes;
            int bytesRead = inputStream.read(buffer);
            String name = new String(inputFile.getName());
            for (int t = 0; t < name.length(); t++) {
                outputStream.write(name.charAt(t));
            }
            outputStream.write('/');
            bytes = decodeBinary(temp.getTrash());
            outputStream.write(bytes);
            outputStream.write('/');
            String chara = temp.getchara();
            for (int t = 0; t < chara.length(); t++) {
                outputStream.write(chara.charAt(t));
            }
            outputStream.write(Byte.toUnsignedInt((byte) chara.charAt(chara.length() - 1)));
            while ((bytesRead) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    sb.append(codes[Byte.toUnsignedInt(buffer[i])]);
                }
                String tempo = sb.toString();
                int size8 = tempo.length() % 8;
                if (tempo.length() < 8) {
                    break;
                }
                if (size8 == 0) {
                    bytes = decodeBinary(tempo);
                    sb.setLength(0);
                } else {
                    bytes = decodeBinary(tempo.substring(0, tempo.length() - size8));
                    sb = new StringBuilder(tempo.substring(tempo.length() - size8, tempo.length()));
                }
                outputStream.write(bytes);
                bytesRead = inputStream.read(buffer);
            }
            if (sb.length() != 0) {
                int reg = 8 - sb.length();
                for (int l = 0; l < (reg); l++) {
                    sb.append("0");
                }
                outputStream.write(decodeBinary(sb.toString()));
                outputStream.write(reg);
            } else {
                sb.append("00000000");
                outputStream.write(decodeBinary(sb.toString()));
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    public static Boolean decompress(File inputFile) {
        try (InputStream inputStream = new FileInputStream(inputFile);) {
            byte[] buffer = new byte[4096];
            int bytesRead = inputStream.read(buffer);
            int stage = 0;
            BiTree tree = new BiTree();
            StringBuilder sb = new StringBuilder(inputFile.getAbsolutePath());
            if (sb.toString().contains(".")) {
                sb.delete(sb.lastIndexOf("."), sb.length());
            }
            sb.append("/");
            StringBuilder sb1 = new StringBuilder();
            int qkw;
            for (qkw = 0; qkw < bytesRead; qkw++) {
                if (buffer[qkw] != 10) {
                    if ((char) buffer[qkw] == '/') {
                        qkw++;
                        break;
                    } else {
                        sb1.append((char) buffer[qkw]);
                    }
                }
            }
            File outputFile = new File(sb.toString());
            if (!outputFile.exists()) {
                outputFile.mkdirs();
            }
            OutputStream outputStream = new FileOutputStream(new File(outputFile, sb1.toString()));
            sb.setLength(0);
            while ((bytesRead) != -1) {
                for (int i = qkw; i < bytesRead; i++) {
                    if (stage == 0) {
                        if ((char) buffer[i] == '/') {
                            stage = 1;
                        } else {
                            int b = Byte.toUnsignedInt(buffer[i]);
                            String s = Integer.toBinaryString(b);
                            s = String.format("%8s", s).replace(' ', '0');
                            sb.append(s);
                        }
                    } else if (stage == 1) {
                        while (sb.charAt(0) == '0') {
                            Node tempk = new Node(0);
                            tree.add(tempk);
                            sb.deleteCharAt(0);
                        }
                        Node tempk = new Node(Byte.toUnsignedInt(buffer[i]));
                        tempk.lflag = 1;
                        tree.add(tempk);
                        sb.deleteCharAt(0);
                        if (buffer[i + 1] == buffer[i]) {
                            i++;
                            sb.setLength(0);
                            stage = 2;
                        }
                    } else {
                        int b = Byte.toUnsignedInt(buffer[i]);
                        String s = Integer.toBinaryString(b);
                        s = String.format("%8s", s).replace(' ', '0');
                        sb.append(s);
                    }
                }
                qkw = 0;
                int otk = bytesRead;
                bytesRead = inputStream.read(buffer);
                if (bytesRead == -1 && otk != buffer.length) {
                    String temoq = sb.substring(sb.length() - 8, sb.length());
                    int pan = Integer.parseInt(temoq, 2);
                    sb.delete(sb.length() - 8, sb.length());
                    sb.delete(sb.length() - pan, sb.length());
                }
                int counterr = 1;
                while (sb.length() > 0) {
                    if (counterr > sb.length()) {
                        break;
                    }
                    int trans = tree.find(new StringBuilder((sb.substring(0, counterr))));
                    if (trans == -1) {
                        counterr++;
                    } else {
                        outputStream.write(trans);
                        sb.delete(0, counterr);
                        counterr = 1;
                    }
                }
            }
            inputStream.close();
            outputStream.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public static byte[] decodeBinary(String s) {
        byte[] data = new byte[s.length() / 8];
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '1') {
                data[i >> 3] |= 0x80 >> (i & 0x7);
            }
        }
        return data;
    }
}
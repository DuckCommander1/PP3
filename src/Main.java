import java.io.*;
import java.util.*;
import java.util.Comparator;
import com.google.gson.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.zip.*;

abstract class Obj {
    String prodName;
    String modelNumber;
    int ram;
    int driveSpace;
    int price;

    public Obj(String prodName, String modelNumber, int ram, int driveSpace, int price) {
        this.prodName = prodName;
        this.modelNumber = modelNumber;
        this.ram = ram;
        this.driveSpace = driveSpace;
        this.price = price;
    }
    public String getProdName() {
        return prodName;
    }
    public void setProdName(String prodName) {
        this.prodName = prodName;
    }
    public String getModelNumber() {
        return modelNumber;
    }
    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }
    public int getRam() {
        return ram;
    }
    public void setRam(int ram) {
        this.ram = ram;
    }
    public int getDriveSpace() {
        return driveSpace;
    }
    public void setDriveSpace(int driveSpace) {
        this.driveSpace = driveSpace;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public String toString() {
        return prodName + " " + modelNumber + " " + ram + " " + driveSpace + " " + price;
    }
}
abstract class FileIO {
    abstract void readFile(String fileName);
    abstract void writeFile(String fileName);
}
abstract class ObjStorage {
    abstract void addObj(Computer obj);
    abstract void removeObj(Obj obj);
}

class ComputerComparator implements Comparator<Obj> {
    @Override
    public int compare(Obj c1, Obj c2) {
        return Integer.compare(c1.getPrice(), c2.getPrice());
    }
}

class ObjList extends ObjStorage implements Iterable<Computer> {
    List<Computer> phones = new ArrayList<>();

    @Override
    void addObj(Computer obj) {
        phones.add(obj);
    }

    @Override
    void removeObj(Obj obj) {
        phones.remove(obj);
    }

    @Override
    public Iterator<Computer> iterator() {
        return phones.iterator();
    }

    private static ObjList instance;
    public static synchronized ObjList getInstance() {
        if(instance == null) {
            instance = new ObjList();
        }
        return instance;
    }
}

class ObjMap extends ObjStorage {

    Map<String, Obj> phones = new HashMap<>();

    @Override
    void addObj(Computer obj) {
        phones.put(obj.toString(), obj);
    }

    @Override
    void removeObj(Obj obj) {
        phones.remove(obj.toString());
    }

}

class TextFileIO extends FileIO {
    Computer temp;
    ObjList objList = ObjList.getInstance();
    ObjMap objMap = new ObjMap();

    @Override
    void readFile(String fileName) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            String a;

            while ((a = bufferedReader.readLine()) != null) {
                String[] tokens = a.split("[ _!;,?.]+");

                if(tokens.length < 5) {
                    continue;
                }

                try {
                    temp = new Computer(tokens[0], tokens[1], Integer.parseInt(tokens[2].replaceAll("[^\\d]", "")), Integer.parseInt(tokens[3].replaceAll("[^\\d]", "")), Integer.parseInt(tokens[4].replaceAll("[^\\d]", "")));
                    objList.addObj(temp);
                    objMap.addObj(temp);
                }
                catch (IllegalArgumentException e) {
                    System.err.println("Illegal expression, failed to read data!");
                }
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found :(");
        }
        catch (IOException e) {
            System.err.println("Input error!");
        }
    }

    @Override
    void writeFile(String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(writer);

            for (Computer a: objList) {
                if(a != null)
                    out.write(a + System.lineSeparator());
            }

            out.close();
        }
        catch (IOException e) {
            System.err.println("Error writing in file");
        }
    }

    void readJson(String fileName) {
        String a;
        Computer temp;
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            while ((a = bufferedReader.readLine()) != null) {
                temp = gson.fromJson(a, Computer.class);
                objList.addObj(temp);
                objMap.addObj(temp);
            }
        }
        catch (IOException e) {
            System.err.println("Input error!");
        }
    }

    void writeJson(String fileName) {
        Computer temp;
        String json;
        Gson gson = new Gson();
        try {
            FileWriter writer = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(writer);
            for(Computer a: objList) {
                json = gson.toJson(a);
                out.write(json + System.lineSeparator());
            }

            out.close();
        }
        catch (IOException e) {
            System.err.println("Error writing in file");
        }
    }

    void writeXML(String fileName) {
        String a;
        try {
            JAXBContext context = JAXBContext.newInstance(Computer.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            for (Computer temp: objList) {
                marshaller.marshal(temp, new File(fileName));
            }
        }
        catch (JAXBException e) {
            System.out.println("Reading error .xml");
        }
    }

    void zipFile(String filename) {
        try {
            FileOutputStream fos = new FileOutputStream("D:/archive.zip");
            ZipOutputStream zip = new ZipOutputStream(fos);

            FileInputStream fis = new FileInputStream(filename);
            ZipEntry zipEntry = new ZipEntry(new File(filename).getName());

            zip.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;

            while((length = fis.read(bytes)) >= 0) {
                zip.write(bytes, 0, length);
            }

            fis.close();
            zip.closeEntry();
            zip.close();
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    void delete(int num) {
        if(num < objList.phones.size() && num > 0) {
            Obj a = objList.phones.get(num);
            objList.phones.remove(a);
            objMap.phones.remove(a);
        }
    }
    void update(int num) {
        Scanner scan = new Scanner(System.in);
        Obj temp;
        if(num <= objList.phones.size()) {
            System.out.println(objList.phones.get(num));

            System.out.println("Print option");
            int n = scan.nextInt();
            temp = objList.phones.get(num);

            System.out.println("New value");
            String a = scan.next();
            objList.removeObj(temp);
            objMap.removeObj(temp);
            try {
                switch (n) {
                    case 0:
                        temp.setProdName(a);
                        break;
                    case 1:
                        temp.setModelNumber(a);
                        break;
                    case 2:
                        temp.setRam(Integer.parseInt(a));
                        break;
                    case 3:
                        temp.setDriveSpace(Integer.parseInt(a));
                        break;
                    case 4:
                        temp.setPrice(Integer.parseInt(a));
                        break;
                }
            }
            catch (IllegalArgumentException e) {
                System.err.println("Failed to update");
            }

        }
    }
}

class Computer extends Obj {
    public Computer(String prodName, String modelNumber, int ram, int driveSpace, int price) {
        super(prodName, modelNumber, ram, driveSpace, price);
        if (ram <= 0) {
            throw new IllegalArgumentException();
        }
        if(driveSpace <= 0) {
            throw new IllegalArgumentException();
        }
        if(price < 0) {
            throw new IllegalArgumentException();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TextFileIO txt = new TextFileIO();

        System.out.println("Input file name");
        String inputName = scanner.nextLine();
        System.out.println("Output file name");
        String outputName = scanner.nextLine();

        txt.readJson(inputName + ".json");
        for(Object a: txt.objList.phones) {
            System.out.println(a);
        }
        //txt.delete(1);

        if(txt.objList.phones != null) {
            txt.objList.phones.sort(new ComputerComparator());
        }

        System.out.println("After sort");
        for(Computer a : txt.objList) {
            System.out.println(a);
        }

        txt.writeFile(outputName + ".txt");
        txt.writeJson(outputName + ".json");
        txt.writeXML(outputName + ".xml");
        txt.zipFile(outputName + ".txt");
    }
}
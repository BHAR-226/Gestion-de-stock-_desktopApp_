package marchandise;

import java.util.Objects;
import java.util.Scanner;

/**
 * Caractéristiques techniques d'un appareil.
 */
public class Caracteristic {
    private String ram;
    private String memoire;
    private String cpu;
    private String gpu;
    private String size;

    public Caracteristic() {}

    public Caracteristic(String ram, String memoire, String cpu, String gpu, String size) {
        this.ram = ram;
        this.memoire = memoire;
        this.cpu = cpu;
        this.gpu = gpu;
        this.size = size;
    }

    // Getters / Setters
    public String getRam() { return ram; }
    public void setRam(String ram) { this.ram = ram; }

    public String getMemoire() { return memoire; }
    public void setMemoire(String memoire) { this.memoire = memoire; }

    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }

    public String getGpu() { return gpu; }
    public void setGpu(String gpu) { this.gpu = gpu; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    @Override
    public String toString() {
        return "Caracteristic{" +
                "ram='" + ram + '\'' +
                ", memoire='" + memoire + '\'' +
                ", cpu='" + cpu + '\'' +
                ", gpu='" + gpu + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Caracteristic that = (Caracteristic) o;
        return Objects.equals(ram, that.ram) &&
                Objects.equals(memoire, that.memoire) &&
                Objects.equals(cpu, that.cpu) &&
                Objects.equals(gpu, that.gpu) &&
                Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ram, memoire, cpu, gpu, size);
    }

    /**
     * Crée une Caracteristic via la console.
     */
    public static Caracteristic createFromScanner(Scanner sc) {
        System.out.print("RAM : ");
        String ram = sc.nextLine().trim();

        System.out.print("Mémoire : ");
        String memoire = sc.nextLine().trim();

        System.out.print("CPU : ");
        String cpu = sc.nextLine().trim();

        System.out.print("GPU : ");
        String gpu = sc.nextLine().trim();

        System.out.print("Size : ");
        String size = sc.nextLine().trim();

        return new Caracteristic(ram, memoire, cpu, gpu, size);
    }
}

package database.entities;

public class ClientComputer {
    private int pc_id;
//  private   String mb_serial;
    private String mac_address;

    public ClientComputer(int pc_id, String mac_address) {
        this.pc_id = pc_id;
        this.mac_address = mac_address;
    }

    public int getPc_id() {
        return pc_id;
    }

//    public String getMb_serial() {
//        return mb_serial;
//    }

    public String getMac_address() {
        return mac_address;
    }
}

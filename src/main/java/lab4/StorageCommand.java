package lab4;

public class StorageCommand {

    private final int packageID;
    private final StorageMessage storageMessage;


    public StorageCommand(int packageID, StorageMessage storageMessage) {
        this.packageID = packageID;
        this.storageMessage = storageMessage;
    }

    public int getPackageID() {
        return packageID;
    }

    public StorageMessage getStorageMessage() {
        return storageMessage;
    }
}

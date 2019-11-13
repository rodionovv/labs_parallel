public class StorageCommand {

    private final int storageID;
    private final StorageMessage storageMessage;


    public StorageCommand(int storageID, StorageMessage storageMessage) {
        this.storageID = storageID;
        this.storageMessage = storageMessage;
    }

    public int getStorageID() {
        return storageID;
    }

    public StorageMessage getStorageMessage() {
        return storageMessage;
    }
}

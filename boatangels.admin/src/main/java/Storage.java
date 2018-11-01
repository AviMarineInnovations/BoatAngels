import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;

public class Storage {
    public static void getBoatPhotos(FirebaseApp app, String localDir, String remoteDir){
        Bucket devBucket = StorageClient.getInstance(app).bucket();

        Page<Blob> blobs = devBucket.list();
        Iterator<Blob> blobIterator = blobs.iterateAll().iterator();
        new File(localDir.isEmpty()?remoteDir:localDir + "\\" + remoteDir).mkdirs();
        while (blobIterator.hasNext()) {
            Blob blob = blobIterator.next();
            System.out.println(blob);
            if (blob.getName().equals(remoteDir + "/")){
                continue;
            }
            Path path = FileSystems.getDefault().getPath(blob.getName());
            try {
                path.toFile().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            blob.downloadTo(path);
        }
    }
}

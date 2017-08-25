package Text;
/**
 * Created by butter on 16-11-21.
 */
public interface TextDAO {
    void    create(String file);
    void    save(String s, String file);
    String  read(String file);
}
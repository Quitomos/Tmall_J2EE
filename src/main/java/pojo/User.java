package pojo;


public class User {
    private String name;
    private String password;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAnonymity() {
        if (null == name) return null;
        int n = name.length();
        if (n == 1) return "*";
        if (n == 2) return name.substring(0, 1) + "*";
        char[] cs = name.toCharArray();
        for (int i = 1; i < n - 1; ++i) {
            cs[i] = '*';
        }
        return new String(cs);
    }
}

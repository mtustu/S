package mkw.ceit.edu.s;

public class testclass {
    private String title,desc,image,username,profileImage;

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public testclass()
    {

    }
    public testclass(String title,String desc,String image,String username,String profileIamge)
    {
        this.title=title;
        this.desc=desc;
        this.image=image;
        this.username = username;
        this.profileImage=profileIamge;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

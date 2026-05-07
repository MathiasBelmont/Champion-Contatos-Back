package champion.com.demo.domain.user;

public enum UserRole {
    AGENTE("AGENTE"),
    GESTOR("GESTOR"),
    GESTOR_TI("GESTOR_TI");

    private String role;

    UserRole(String role){
        this.role = role;
    }
    
    public String getRole(){
        return role;
    }
}
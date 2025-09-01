package ages.hopeful.modules.users.services;

import ages.hopeful.modules.users.dto.UserResponse;
import ages.hopeful.modules.users.models.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UsersService {

    private final List<User> mockDb = new ArrayList<>(List.of(
            User.builder().id(1L).nome("Ana Silva").cpf("111.111.111-11").email("ana@example.com")
                    .telefone("11999990000")
                    .estado("SP").cidade("São Paulo").serviço("Saúde").status(true).build(),
            User.builder().id(2L).nome("Bruno Souza").cpf("222.222.222-22").email("bruno@example.com")
                    .telefone("21988887777").estado("RJ").cidade("Rio de Janeiro").serviço("Defesa Civil").status(false)
                    .build(),
            User.builder().id(3L).nome("Carla Dias").cpf("333.333.333-33").email("carla@example.com")
                    .telefone("31977776666").estado("MG").cidade("Belo Horizonte").serviço("Assistência Social")
                    .status(true)
                    .build()));

    public List<UserResponse> list(String status) {
        try{
            if(!status.isEmpty()){
            String s = status.trim().toLowerCase();
            Boolean filter;
            switch (s) {
                case "ativo" -> filter = true;
                case "inativo" -> filter = false;
                default -> throw new IllegalArgumentException("Invalid status filter. Use 'ativo' or 'inativo'.");
            }

            return mockDb.stream()
                .filter(u -> u.isStatus() == filter)
                .sorted(Comparator.comparing(User::getNome, String.CASE_INSENSITIVE_ORDER))
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getNome(),
                        u.getCpf(),
                        u.getEmail(),
                        u.getTelefone(),
                        u.getEstado(),
                        u.getCidade(),
                        u.getServiço(),
                        u.isStatus()))
                .toList();
        }

        return mockDb.stream()
                .sorted(Comparator.comparing(User::getNome, String.CASE_INSENSITIVE_ORDER))
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getNome(),
                        u.getCpf(),
                        u.getEmail(),
                        u.getTelefone(),
                        u.getEstado(),
                        u.getCidade(),
                        u.getServiço(),
                        u.isStatus()))
                .toList();
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return null; 
    }
}

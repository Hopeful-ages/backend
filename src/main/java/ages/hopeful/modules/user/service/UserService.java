package ages.hopeful.modules.user.service;

import ages.hopeful.exception.ConflictException;
import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.mapper.UserMapper;
import ages.hopeful.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponseDTO getUserById(java.util.UUID id) {
        return UserMapper.toResponse(
            userRepository
                .findById(id)
                .orElseThrow(() ->
                    new ConflictException("Usuário não encontrado")
                )
        );
    }

    @Transactional
    public String updateUser(java.util.UUID id, UserUpdateDTO userUpdateDTO) {
        var user = userRepository
            .findById(id)
            .orElseThrow(() -> new ConflictException("Usuário não existe"));

        UserMapper.updateEntity(user, userUpdateDTO);
        userRepository.save(user);

        return "Usuário atualizado com sucesso";
    }
}

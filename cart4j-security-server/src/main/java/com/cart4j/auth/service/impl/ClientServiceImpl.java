package com.cart4j.auth.service.impl;

import com.cart4j.auth.entity.Client;
import com.cart4j.auth.entity.Scope;
import com.cart4j.auth.exception.ClientNotFoundException;
import com.cart4j.auth.exception.ScopeNotFoundException;
import com.cart4j.auth.repository.ClientRepository;
import com.cart4j.auth.repository.ScopeRepository;
import com.cart4j.auth.service.ClientService;
import com.cart4j.model.security.dto.v1.ClientDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, PasswordEncoder passwordEncoder, ScopeRepository scopeRepository) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.scopeRepository = scopeRepository;
    }

    @Override
    public Page<ClientDto> getClients(Pageable pageable, String searchKey) {
        Specification<Client> spec = null;
        if(!StringUtils.isEmpty(searchKey)) {
            spec = ClientSpec.searchKey(searchKey);
        }
        return clientRepository.findAll(spec, pageable).map(Client::toDto);
    }

    @Override
    public ClientDto setScopes(List<Long> scopeIds, Long clientId) {

        Client client = clientRepository.getOne(clientId);
        Set<Long> addingIds = new HashSet<>(scopeIds);
        if(!CollectionUtils.isEmpty(client.getScopes())) {
            for(Scope scope: client.getScopes()) {
                if(!addingIds.contains(scope.getId())) {
                    scopeRepository.deleteById(scope.getId());
                } else {
                    addingIds.remove(scope.getId());
                }
            }
        }

        if(!CollectionUtils.isEmpty(addingIds)) {
            client.setScopes(addingIds.stream().map(scopeRepository::getOne).collect(Collectors.toList()));
        }
        return clientRepository.save(client).toDto();
    }

    @Override
    public ClientDto addScope(Long scopeId, Long clientId) throws ScopeNotFoundException, ClientNotFoundException {
        if(!scopeRepository.existsById(scopeId)) {
            throw new ScopeNotFoundException("Scope[" + scopeId + "] does not exist");
        }
        Scope scope = scopeRepository.getOne(scopeId);

        if(!clientRepository.existsById(clientId)) {
            throw new ClientNotFoundException("Client[" + clientId + "] does not exist");
        }
        Client client = clientRepository.getOne(clientId);

        if(CollectionUtils.isEmpty(client.getScopes())) {
            client.setScopes(Arrays.asList(scope));
        } else {
            client.getScopes().add(scope);
        }

        return clientRepository.save(client).toDto();
    }

    @Override
    public ClientDto getClient(Long id) {
        if(!clientRepository.existsById(id)) {
            return null;
        }
        return clientRepository.getOne(id).toDto();
    }

    @Override
    public ClientDto getClientByClientUniqueId(String clientUniqueId) {
        return clientRepository.findByClientUniqueId(clientUniqueId).toDto();
    }

    @Override
    public ClientDto addClient(ClientDto client) throws ClientAlreadyExistsException {
        if(clientRepository.existsByClientUniqueId(client.getClientUniqueId())) {
            throw new ClientAlreadyExistsException("ClientId is already existing");
        }
        Client newClient = Client.builder()
                .clientSecret(passwordEncoder.encode(client.getClientSecret()))
                .clientUniqueId(client.getClientUniqueId())
                .grantTypes(client.getGrantTypes())
                .build();

        return clientRepository.save(newClient).toDto();
    }

    @Override
    public ClientDto editClient(Long id, ClientDto client) {
        Client modifyingClient = clientRepository.getOne(id);
        if(!StringUtils.isEmpty(client.getClientSecret())) {
            modifyingClient.setClientSecret(passwordEncoder.encode(client.getClientSecret()));
        }
        modifyingClient.setGrantTypes(client.getGrantTypes());

        return clientRepository.save(modifyingClient).toDto();
    }

    @Override
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    private static class ClientSpec {
        private static Specification<Client> searchKey(String searchKey) {
            return (root, query, builder) -> builder.like(root.get("clientUniqueId"), "%" + searchKey + "%");
        }
    }

    private final ClientRepository clientRepository;

    private final PasswordEncoder passwordEncoder;

    private final ScopeRepository scopeRepository;
}

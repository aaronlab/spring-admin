package com.example.admin.service;

import com.example.admin.ifs.CrudInterface;
import com.example.admin.model.entity.Item;
import com.example.admin.model.network.Header;
import com.example.admin.model.network.request.ItemApiRequest;
import com.example.admin.model.network.response.ItemApiResponse;
import com.example.admin.repository.ItemRepository;
import com.example.admin.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemApiLogicService implements CrudInterface<ItemApiRequest, ItemApiResponse> {

    private final PartnerRepository partnerRepository;
    private final ItemRepository itemRepository;

    @Override
    public Header<ItemApiResponse> create(Header<ItemApiRequest> request) {

        ItemApiRequest body = request.getData();

        Item item = Item.builder()
                .status(body.getStatus())
                .name(body.getName())
                .title(body.getTitle())
                .content(body.getContent())
                .price(body.getPrice())
                .brandName(body.getBrandName())
                .registeredAt(LocalDateTime.now())
                .partner(partnerRepository.getOne(body.getPartnerId()))
                .build();

        Item newItem = itemRepository.save(item);

        return response(newItem);
    }

    @Override
    public Header<ItemApiResponse> read(Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);

        return optionalItem
                .map(this::response)
                .orElseGet(() -> Header.ERROR("no item data"));
    }

    @Override
    public Header<ItemApiResponse> update(Header<ItemApiRequest> request) {

        ItemApiRequest body = request.getData();

        Optional<Item> optionalItem = itemRepository.findById(body.getId());

        return optionalItem
                .map(item -> {
                    item
                            .setStatus(body.getStatus())
                            .setName(body.getStatus())
                            .setTitle(body.getName())
                            .setContent(body.getContent())
                            .setPrice(body.getPrice())
                            .setBrandName(body.getBrandName())
                            .setUpdatedAt(LocalDateTime.now())
                            .setPartner(partnerRepository.getOne(body.getPartnerId()));
                    return item;
                })
                .map(itemRepository::save)
                .map(this::response)
                .orElseGet(() -> Header.ERROR("no item data"));
    }

    @Override
    public Header delete(Long id) {

        Optional<Item> optionalItem = itemRepository.findById(id);

        return optionalItem
                .map(item -> {
                    itemRepository.delete(item);

                    return Header.OK(item);
                })
                .orElseGet(() -> Header.ERROR("no item data"));
    }

    private Header<ItemApiResponse> response(Item item) {
        ItemApiResponse body = ItemApiResponse.builder()
                .id(item.getId())
                .status(item.getStatus())
                .name(item.getName())
                .title(item.getTitle())
                .content(item.getContent())
                .price(item.getPrice())
                .brandName(item.getBrandName())
                .registeredAt(item.getRegisteredAt())
                .unregisteredAt(item.getUnregisteredAt())
                .partnerId(item.getPartner().getId())
                .build();

        return Header.OK(body);
    }
}
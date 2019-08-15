package com.cart4j.model.cart.dto.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartDtoV1 {
    private Long id;
    /**
     * Session will be created for each client connection and remaining until the expiry date.
     */
    private String session;
    /**
     * Username. For a visitor, this field will be empty when carts allows anonymous user.
     * After a user put items into their cart without logged in and then log in, the list of items will be merge into the user's cart.
     */
    private String username;
    /**
     * Account ID.
     * Cart4j can provide multiple accounts. It means you can create multiple stores and storing all information separately.
     */
    private Long accountId;

    private List<CartItemDtoV1> cartItems;

    private Date addedAt;
}

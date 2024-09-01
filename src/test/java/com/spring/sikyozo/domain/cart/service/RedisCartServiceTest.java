//package com.spring.sikyozo.domain.cart.service;
//
//import com.spring.sikyozo.domain.cart.entity.CartItem;
//import com.spring.sikyozo.domain.menu.entity.Menu;
//import com.spring.sikyozo.domain.menu.repository.MenuRepositoryImpl;
//import com.spring.sikyozo.domain.store.entity.Store;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.lang.reflect.Field;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class RedisCartServiceTest {
//
//    @Mock
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Mock
//    private HashOperations<String, Object, Object> hashOperations;
//
//    @Mock
//    private MenuRepositoryImpl menuRepository;
//
//    @InjectMocks
//    private RedisCartService cartService;
//
//    @BeforeEach
//    void setup() throws NoSuchFieldException, IllegalAccessException {
//
//        Store store1 = new Store();
//        setField(store1, "id", UUID.randomUUID());
//        Store store2 = new Store();
//        setField(store2, "id", UUID.randomUUID());
//
//        UUID uuid1 = UUID.fromString("0f39adfd-d38e-4206-92f2-0cc68a2a6af8");
//        Menu menu1 = new Menu();
//        setField(menu1, "id", uuid1);
//        setField(menu1, "menuName", "양념 치킨");
//        setField(menu1, "price", 16000L);
//        setField(menu1, "store", store1);
//
//        UUID uuid2 =  UUID.fromString("8910f765-d42d-40f3-ab25-432d63bbf7fc");
//        Menu menu2 = new Menu();
//        setField(menu2, "id", uuid2);
//        setField(menu2, "menuName", "후라이드 치킨");
//        setField(menu2, "price", 15000L);
//        setField(menu2, "store", store1);
//
//        UUID uuid3 = UUID.fromString("9f025ecb-3918-46e7-823d-e4c954d2b64d");
//        Menu menu3 = new Menu();
//        setField(menu3, "id", uuid3);
//        setField(menu3, "menuName", "훈제 치킨");
//        setField(menu3, "price", 18000L);
//        setField(menu3, "store", store2);
//
//        given(menuRepository.findById(menu1.getId())).willReturn(Optional.of(menu1));
//        // Mock RedisTemplate hash operations
//        given(redisTemplate.opsForHash()).willReturn(hashOperations);
//
//        CartItem cartItem = CartItem.create(menu1, 3);
//
//        // Mock the result from Redis
//        given(hashOperations.values("cart:1")).willReturn(List.of(cartItem));
//    }
//
//
//    private void setField(Object targetObject, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
//        Field field = targetObject.getClass().getDeclaredField(fieldName);
//        field.setAccessible(true); // private 필드에 접근 가능하게 설정
//        field.set(targetObject, value);
//    }
//
//    @Test
//    void addCartItemTest() {
//
//        Long userId = 1L;
//        UUID menuId = UUID.fromString("0f39adfd-d38e-4206-92f2-0cc68a2a6af8");
//        Integer quantity = 3;
//
//        Menu menu = menuRepository.findById(menuId).get();
//
//        // When
//        cartService.addOrUpdateCartItem(userId, menu.getId(), quantity);
//
//        // then
//        verify(hashOperations).put(eq("cart:1"), eq(menuId.toString()), any(CartItem.class));
//
//    }
//
//}
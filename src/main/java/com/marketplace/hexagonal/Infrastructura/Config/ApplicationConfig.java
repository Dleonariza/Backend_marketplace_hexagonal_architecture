package com.marketplace.hexagonal.Infrastructura.Config;

import com.marketplace.hexagonal.Application.Chat.Services.ChatService;
import com.marketplace.hexagonal.Application.Chat.UseCases.*;
import com.marketplace.hexagonal.Application.Admin.Services.AdminService;
import com.marketplace.hexagonal.Application.Admin.UseCases.*;
import com.marketplace.hexagonal.Application.Cart.Services.CartService;
import com.marketplace.hexagonal.Application.Cart.UseCases.*;
import com.marketplace.hexagonal.Application.Coupon.Services.CouponService;
import com.marketplace.hexagonal.Application.Coupon.UseCases.*;
import com.marketplace.hexagonal.Application.Product.Services.CategoryService;
import com.marketplace.hexagonal.Application.Product.Services.ProductService;
import com.marketplace.hexagonal.Application.Product.UseCases.*;
import com.marketplace.hexagonal.Application.Seller.Services.SellerService;
import com.marketplace.hexagonal.Application.Seller.UseCases.*;
import com.marketplace.hexagonal.Application.Seller.UseCases.UpdateSellerProfileUseCaseImpl;
import com.marketplace.hexagonal.Application.Favorite.Services.FavoriteService;
import com.marketplace.hexagonal.Application.Favorite.UseCases.*;
import com.marketplace.hexagonal.Application.Review.Services.ReviewService;
import com.marketplace.hexagonal.Application.Review.UseCases.*;
import com.marketplace.hexagonal.Application.User.Services.AddressService;
import com.marketplace.hexagonal.Application.User.Services.PaymentMethodService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Application.User.UseCases.*;
import com.marketplace.hexagonal.Application.Shipping.Services.ShippingService;
import com.marketplace.hexagonal.Application.Shipping.UseCases.*;
import com.marketplace.hexagonal.Application.Order.UseCases.*;
import com.marketplace.hexagonal.Application.Order.UseCases.CancelOrderUseCaseImpl;
import com.marketplace.hexagonal.Application.Order.Services.OrderService;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatRoomRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Adapters.JpaChatRoomRepositoryAdapter;
import com.marketplace.hexagonal.Infrastructura.Adapters.JpaChatMessageRepositoryAdapter;
import com.marketplace.hexagonal.Application.Inquiry.Services.InquiryService;
import com.marketplace.hexagonal.Application.Inquiry.UseCases.*;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Adapters.JpaInquiryThreadRepositoryAdapter;
import com.marketplace.hexagonal.Infrastructura.Adapters.JpaInquiryMessageRepositoryAdapter;
import com.marketplace.hexagonal.Domain.Admin.Ports.Out.AdminStatsRepositoryPort;
import com.marketplace.hexagonal.Domain.Admin.Ports.Out.CommissionConfigRepositoryPort;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.StockReservationRepositoryPort;
import com.marketplace.hexagonal.Domain.Coupon.Ports.Out.CouponRepositoryPort;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderNumberGeneratorPort;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.SubOrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.CategoryRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.Shipping.Ports.Out.SellerShippingOptionRepositoryPort;
import com.marketplace.hexagonal.Domain.Favorite.Ports.Out.FavoriteRepositoryPort;
import com.marketplace.hexagonal.Domain.Review.Ports.Out.ReviewRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Ports.Out.PaymentMethodRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserAddressRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Adapters.*;
import com.marketplace.hexagonal.Infrastructura.Adapters.JpaFavoriteRepositoryAdapter;
import com.marketplace.hexagonal.Infrastructura.Adapters.JpaReviewRepositoryAdapter;
import com.marketplace.hexagonal.Infrastructura.Mappers.OrderMapper;
import org.springframework.context.ApplicationEventPublisher;
import com.marketplace.hexagonal.Infrastructura.Mappers.ShippingOptionMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaOrderRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaSellerProfileRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaSellerShippingOptionRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaSubOrderRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;

import com.marketplace.hexagonal.Infrastructura.Security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@org.springframework.scheduling.annotation.EnableScheduling
public class ApplicationConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public UserService userService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        return new UserService(
                new CreateUserUseCaseImpl(userRepositoryPort),
                new FindUserByNameUseCaseImpl(userRepositoryPort),
                new FindAllUseCaseImpl(userRepositoryPort),
                new DeleteUserUseCaseImpl(userRepositoryPort),
                new FindUserByIdUseCaseImpl(userRepositoryPort),
                new UpdateUserProfileUseCaseImpl(userRepositoryPort),
                new SearchUsersUseCaseImpl(userRepositoryPort),
                new BlockUserUseCaseImpl(userRepositoryPort),
                new UnblockUserUseCaseImpl(userRepositoryPort),
                passwordEncoder,
                userRepositoryPort
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean @Primary
    public UserRepositoryPort userRepositoryPort(JpaUserRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean @Primary
    public UserAddressRepositoryPort userAddressRepositoryPort(JpaUserAddressRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public AddressService addressService(UserAddressRepositoryPort userAddressRepositoryPort) {
        return new AddressService(
                new GetAddressesUseCaseImpl(userAddressRepositoryPort),
                new AddAddressUseCaseImpl(userAddressRepositoryPort),
                new UpdateAddressUseCaseImpl(userAddressRepositoryPort),
                new DeleteAddressUseCaseImpl(userAddressRepositoryPort),
                new SetDefaultAddressUseCaseImpl(userAddressRepositoryPort)
        );
    }

    @Bean @Primary
    public PaymentMethodRepositoryPort paymentMethodRepositoryPort(
            JpaPaymentMethodRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public PaymentMethodService paymentMethodService(
            PaymentMethodRepositoryPort paymentMethodRepositoryPort) {
        return new PaymentMethodService(
                new AddPaymentMethodUseCaseImpl(paymentMethodRepositoryPort),
                new ListPaymentMethodsUseCaseImpl(paymentMethodRepositoryPort),
                new DeletePaymentMethodUseCaseImpl(paymentMethodRepositoryPort),
                new SetDefaultPaymentMethodUseCaseImpl(paymentMethodRepositoryPort)
        );
    }

    @Bean @Primary
    public FavoriteRepositoryPort favoriteRepositoryPort(JpaFavoriteRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public FavoriteService favoriteService(FavoriteRepositoryPort favoriteRepositoryPort,
                                           ProductRepositoryPort productRepositoryPort) {
        return new FavoriteService(
                new AddFavoriteUseCaseImpl(favoriteRepositoryPort, productRepositoryPort),
                new RemoveFavoriteUseCaseImpl(favoriteRepositoryPort),
                new ListFavoritesUseCaseImpl(favoriteRepositoryPort)
        );
    }

    @Bean @Primary
    public SellerProfileRepositoryPort sellerProfileRepositoryPort(JpaSellerProfileRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public SellerService sellerService(SellerProfileRepositoryPort sellerProfileRepositoryPort,
                                       UserRepositoryPort userRepositoryPort,
                                       ApplicationEventPublisher eventPublisher) {
        return new SellerService(
                new ApplySellerUseCaseImpl(sellerProfileRepositoryPort),
                new ApproveSellerUseCaseImpl(sellerProfileRepositoryPort, userRepositoryPort),
                new RejectSellerUseCaseImpl(sellerProfileRepositoryPort),
                new SuspendSellerUseCaseImpl(sellerProfileRepositoryPort),
                new ReactivateSellerUseCaseImpl(sellerProfileRepositoryPort),
                new CancelSellerApplicationUseCaseImpl(sellerProfileRepositoryPort),
                new BanSellerUseCaseImpl(sellerProfileRepositoryPort),
                new GetSellerProfileUseCaseImpl(sellerProfileRepositoryPort),
                new GetSellerApplicationsUseCaseImpl(sellerProfileRepositoryPort),
                new UpdateSellerProfileUseCaseImpl(sellerProfileRepositoryPort),
                eventPublisher
        );
    }

    @Bean @Primary
    public CategoryRepositoryPort categoryRepositoryPort(JpaCategoryRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean @Primary
    public ProductRepositoryPort productRepositoryPort(JpaProductRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public CategoryService categoryService(CategoryRepositoryPort categoryRepositoryPort) {
        return new CategoryService(
                new CreateCategoryUseCaseImpl(categoryRepositoryPort),
                new UpdateCategoryUseCaseImpl(categoryRepositoryPort),
                new GetCategoryUseCaseImpl(categoryRepositoryPort),
                new ListCategoriesUseCaseImpl(categoryRepositoryPort),
                new ActivateCategoryUseCaseImpl(categoryRepositoryPort),
                new DeactivateCategoryUseCaseImpl(categoryRepositoryPort)
        );
    }

    @Bean
    public ProductService productService(ProductRepositoryPort productRepositoryPort,
                                         CategoryRepositoryPort categoryRepositoryPort,
                                         ApplicationEventPublisher eventPublisher) {
        return new ProductService(
                new CreateProductUseCaseImpl(productRepositoryPort, categoryRepositoryPort),
                new UpdateProductUseCaseImpl(productRepositoryPort, categoryRepositoryPort),
                new PublishProductUseCaseImpl(productRepositoryPort),
                new DeactivateProductUseCaseImpl(productRepositoryPort),
                new ReactivateProductUseCaseImpl(productRepositoryPort),
                new DeleteProductUseCaseImpl(productRepositoryPort),
                new GetProductUseCaseImpl(productRepositoryPort),
                new ListProductsUseCaseImpl(productRepositoryPort),
                new SearchProductsUseCaseImpl(productRepositoryPort),
                new AddProductVariantUseCaseImpl(productRepositoryPort),
                new UpdateProductVariantUseCaseImpl(productRepositoryPort),
                new RemoveProductVariantUseCaseImpl(productRepositoryPort),
                new AddProductImageUseCaseImpl(productRepositoryPort),
                new RemoveProductImageUseCaseImpl(productRepositoryPort),
                new SetPrimaryImageUseCaseImpl(productRepositoryPort),
                eventPublisher
        );
    }

    @Bean @Primary
    public CartRepositoryPort cartRepositoryPort(JpaCartRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean @Primary
    public StockReservationRepositoryPort stockReservationRepositoryPort(
            JpaStockReservationRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public CartService cartService(CartRepositoryPort cartRepositoryPort,
                                   StockReservationRepositoryPort stockReservationRepositoryPort,
                                   ProductRepositoryPort productRepositoryPort) {
        return new CartService(
                new GetCartUseCaseImpl(cartRepositoryPort),
                new AddToCartUseCaseImpl(cartRepositoryPort, stockReservationRepositoryPort, productRepositoryPort),
                new RemoveFromCartUseCaseImpl(cartRepositoryPort, stockReservationRepositoryPort),
                new UpdateCartItemQuantityUseCaseImpl(cartRepositoryPort, stockReservationRepositoryPort, productRepositoryPort),
                new ClearCartUseCaseImpl(cartRepositoryPort, stockReservationRepositoryPort),
                new GetOrCreateAnonymousCartUseCaseImpl(cartRepositoryPort),
                new MergeCartsUseCaseImpl(cartRepositoryPort, stockReservationRepositoryPort),
                new CheckPriceChangesUseCaseImpl(productRepositoryPort)
        );
    }

    @Bean
    public ExpireCartsUseCaseImpl expireCartsUseCase(CartRepositoryPort cartRepositoryPort,
                                                      StockReservationRepositoryPort stockReservationRepositoryPort) {
        return new ExpireCartsUseCaseImpl(cartRepositoryPort, stockReservationRepositoryPort);
    }

    @Bean @Primary
    public CouponRepositoryPort couponRepositoryPort(JpaCouponRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public CouponService couponService(CouponRepositoryPort couponRepositoryPort,
                                       CartRepositoryPort cartRepositoryPort) {
        return new CouponService(
                new CreateCouponUseCaseImpl(couponRepositoryPort),
                new GetCouponUseCaseImpl(couponRepositoryPort),
                new ListCouponsUseCaseImpl(couponRepositoryPort),
                new ToggleCouponUseCaseImpl(couponRepositoryPort),
                new ApplyCouponUseCaseImpl(cartRepositoryPort, couponRepositoryPort),
                new RemoveCouponUseCaseImpl(cartRepositoryPort)
        );
    }

    @Bean @Primary
    public SellerShippingOptionRepositoryPort sellerShippingOptionRepositoryPort(
            JpaSellerShippingOptionRepository jpaSellerShippingOptionRepository,
            JpaSellerProfileRepository jpaSellerProfileRepository,
            ShippingOptionMapper shippingOptionMapper) {
        return new JpaSellerShippingOptionRepositoryAdapter(
                jpaSellerShippingOptionRepository,
                jpaSellerProfileRepository,
                shippingOptionMapper);
    }

    @Bean
    public ShippingService shippingService(
            SellerShippingOptionRepositoryPort sellerShippingOptionRepositoryPort,
            CartRepositoryPort cartRepositoryPort,
            SellerProfileRepositoryPort sellerProfileRepositoryPort) {
        return new ShippingService(
                new AddShippingOptionUseCaseImpl(sellerShippingOptionRepositoryPort),
                new UpdateShippingOptionUseCaseImpl(sellerShippingOptionRepositoryPort),
                new RemoveShippingOptionUseCaseImpl(sellerShippingOptionRepositoryPort),
                new ToggleShippingOptionUseCaseImpl(sellerShippingOptionRepositoryPort),
                new ListSellerShippingOptionsUseCaseImpl(sellerShippingOptionRepositoryPort),
                new CalculateShippingUseCaseImpl(cartRepositoryPort,
                        sellerShippingOptionRepositoryPort, sellerProfileRepositoryPort)
        );
    }

    @Bean @Primary
    public OrderNumberGeneratorPort orderNumberGeneratorPort(OrderRepositoryPort orderRepositoryPort) {
        return new OrderNumberGeneratorAdapter(orderRepositoryPort);
    }

    @Bean @Primary
    public OrderRepositoryPort orderRepositoryPort(
            JpaOrderRepository jpaOrderRepository,
            JpaUserRepository jpaUserRepository,
            JpaSubOrderRepository jpaSubOrderRepository,
            OrderMapper orderMapper) {
        return new JpaOrderRepositoryAdapter(
                jpaOrderRepository, jpaUserRepository, jpaSubOrderRepository, orderMapper);
    }

    @Bean @Primary
    public SubOrderRepositoryPort subOrderRepositoryPort(
            JpaSubOrderRepository jpaSubOrderRepository,
            JpaOrderRepository jpaOrderRepository,
            OrderMapper orderMapper) {
        return new JpaSubOrderRepositoryAdapter(
                jpaSubOrderRepository, jpaOrderRepository, orderMapper);
    }

    @Bean
    public OrderService orderService(
            OrderRepositoryPort orderRepositoryPort,
            SubOrderRepositoryPort subOrderRepositoryPort,
            CartRepositoryPort cartRepositoryPort,
            SellerShippingOptionRepositoryPort sellerShippingOptionRepositoryPort,
            SellerProfileRepositoryPort sellerProfileRepositoryPort,
            OrderNumberGeneratorPort orderNumberGeneratorPort,
            PaymentMethodRepositoryPort paymentMethodRepositoryPort,
            ApplicationEventPublisher eventPublisher) {
        return new OrderService(
                new PlaceOrderUseCaseImpl(orderRepositoryPort, subOrderRepositoryPort,
                        cartRepositoryPort, sellerShippingOptionRepositoryPort,
                        sellerProfileRepositoryPort, orderNumberGeneratorPort,
                        paymentMethodRepositoryPort),
                new GetOrderUseCaseImpl(orderRepositoryPort),
                new ListUserOrdersUseCaseImpl(orderRepositoryPort),
                new GetSellerSubOrdersUseCaseImpl(subOrderRepositoryPort),
                new UpdateSubOrderStatusUseCaseImpl(subOrderRepositoryPort),
                new CancelOrderUseCaseImpl(orderRepositoryPort),
                new SellerCancelOrderUseCaseImpl(orderRepositoryPort),
                new AdminCancelOrderUseCaseImpl(orderRepositoryPort),
                new ListAllOrdersUseCaseImpl(orderRepositoryPort),
                eventPublisher
        );
    }

    @Bean @Primary
    public ReviewRepositoryPort reviewRepositoryPort(JpaReviewRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public ReviewService reviewService(ReviewRepositoryPort reviewRepositoryPort,
                                       ProductRepositoryPort productRepositoryPort,
                                       SellerProfileRepositoryPort sellerProfileRepositoryPort) {
        return new ReviewService(
                new AddReviewUseCaseImpl(reviewRepositoryPort, productRepositoryPort,
                        sellerProfileRepositoryPort),
                new ListReviewsByTargetUseCaseImpl(reviewRepositoryPort),
                new DeleteReviewUseCaseImpl(reviewRepositoryPort),
                new AdminDeleteReviewUseCaseImpl(reviewRepositoryPort)
        );
    }

    @Bean @Primary
    public ChatRoomRepositoryPort chatRoomRepositoryPort(JpaChatRoomRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean @Primary
    public ChatMessageRepositoryPort chatMessageRepositoryPort(JpaChatMessageRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public ChatService chatService(ChatRoomRepositoryPort chatRoomRepositoryPort,
                                   ChatMessageRepositoryPort chatMessageRepositoryPort,
                                   OrderRepositoryPort orderRepositoryPort,
                                   SellerProfileRepositoryPort sellerProfileRepositoryPort,
                                   ApplicationEventPublisher eventPublisher) {
        return new ChatService(
                new CreateOrGetChatRoomUseCaseImpl(
                        chatRoomRepositoryPort, orderRepositoryPort, sellerProfileRepositoryPort),
                new SendChatMessageUseCaseImpl(
                        chatRoomRepositoryPort, chatMessageRepositoryPort, eventPublisher),
                new GetChatMessagesUseCaseImpl(
                        chatRoomRepositoryPort, chatMessageRepositoryPort),
                new GetUserChatRoomsUseCaseImpl(chatRoomRepositoryPort)
        );
    }

    @Bean @Primary
    public InquiryThreadRepositoryPort inquiryThreadRepositoryPort(JpaInquiryThreadRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean @Primary
    public InquiryMessageRepositoryPort inquiryMessageRepositoryPort(JpaInquiryMessageRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public InquiryService inquiryService(InquiryThreadRepositoryPort inquiryThreadRepositoryPort,
                                         InquiryMessageRepositoryPort inquiryMessageRepositoryPort,
                                         ProductRepositoryPort productRepositoryPort,
                                         SellerProfileRepositoryPort sellerProfileRepositoryPort,
                                         ApplicationEventPublisher eventPublisher) {
        return new InquiryService(
                new CreateOrGetInquiryThreadUseCaseImpl(
                        inquiryThreadRepositoryPort, productRepositoryPort, sellerProfileRepositoryPort),
                new SendInquiryMessageUseCaseImpl(
                        inquiryThreadRepositoryPort, inquiryMessageRepositoryPort, eventPublisher),
                new GetInquiryMessagesUseCaseImpl(
                        inquiryThreadRepositoryPort, inquiryMessageRepositoryPort),
                new GetUserInquiryThreadsUseCaseImpl(inquiryThreadRepositoryPort),
                new CloseInquiryThreadUseCaseImpl(inquiryThreadRepositoryPort)
        );
    }

    @Bean @Primary
    public AdminStatsRepositoryPort adminStatsRepositoryPort(
            JpaAdminStatsRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean @Primary
    public CommissionConfigRepositoryPort commissionConfigRepositoryPort(
            JpaCommissionConfigRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public AdminService adminService(AdminStatsRepositoryPort adminStatsRepositoryPort,
                                     CommissionConfigRepositoryPort commissionConfigRepositoryPort) {
        return new AdminService(
                new GetAdminStatsUseCaseImpl(adminStatsRepositoryPort),
                new GetCommissionConfigUseCaseImpl(commissionConfigRepositoryPort),
                new UpdateCommissionConfigUseCaseImpl(commissionConfigRepositoryPort)
        );
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

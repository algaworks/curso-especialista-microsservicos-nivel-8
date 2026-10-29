(function($) {
	"use strict"

	// Mobile Nav toggle
	$('.menu-toggle > a').on('click', function (e) {
		e.preventDefault();
		$('#responsive-nav').toggleClass('active');
	})

	// Fix cart dropdown from closing
	$('.cart-dropdown').on('click', function (e) {
		e.stopPropagation();
	});

	/////////////////////////////////////////

	// Products Slick
	$('.products-slick').each(function() {
		var $this = $(this),
				$nav = $this.attr('data-nav');

		$this.slick({
			slidesToShow: 4,
			slidesToScroll: 1,
			autoplay: true,
			infinite: true,
			speed: 300,
			dots: false,
			arrows: true,
			appendArrows: $nav ? $nav : false,
			responsive: [{
	        breakpoint: 991,
	        settings: {
	          slidesToShow: 2,
	          slidesToScroll: 1,
	        }
	      },
	      {
	        breakpoint: 480,
	        settings: {
	          slidesToShow: 1,
	          slidesToScroll: 1,
	        }
	      },
	    ]
		});
	});

	// Products Widget Slick
	$('.products-widget-slick').each(function() {
		var $this = $(this),
				$nav = $this.attr('data-nav');

		$this.slick({
			infinite: true,
			autoplay: true,
			speed: 300,
			dots: false,
			arrows: true,
			appendArrows: $nav ? $nav : false,
		});
	});

	/////////////////////////////////////////

	// Product Main img Slick
	$('#product-main-img').slick({
    infinite: true,
    speed: 300,
    dots: false,
    arrows: true,
    fade: true,
    asNavFor: '#product-imgs',
  });

	// Product imgs Slick
  $('#product-imgs').slick({
    slidesToShow: 3,
    slidesToScroll: 1,
    arrows: true,
    centerMode: true,
    focusOnSelect: true,
		centerPadding: 0,
		vertical: true,
    asNavFor: '#product-main-img',
		responsive: [{
        breakpoint: 991,
        settings: {
					vertical: false,
					arrows: false,
					dots: true,
        }
      },
    ]
  });

	// Product img zoom
	var zoomMainProduct = document.getElementById('product-main-img');
	if (zoomMainProduct) {
		$('#product-main-img .product-preview').zoom();
	}

	/////////////////////////////////////////

	// Input number
	$('.input-number').each(function() {
		var $this = $(this),
		$input = $this.find('input[type="number"]'),
		up = $this.find('.qty-up'),
		down = $this.find('.qty-down');

		down.on('click', function () {
			var value = parseInt($input.val()) - 1;
			value = value < 1 ? 1 : value;
			$input.val(value);
			$input.change();
			updatePriceSlider($this , value)
		})

		up.on('click', function () {
			var value = parseInt($input.val()) + 1;
			$input.val(value);
			$input.change();
			updatePriceSlider($this , value)
		})
	});

	var priceInputMax = document.getElementById('price-max');
    var priceInputMin = document.getElementById('price-min');

    if (priceInputMax != null ){
        priceInputMax.addEventListener('change', function(){
            updatePriceSlider($(this).parent() , this.value)
        });
    }

	if (priceInputMin != null ){
        priceInputMin.addEventListener('change', function(){
            updatePriceSlider($(this).parent() , this.value)
        });
	}

	function updatePriceSlider(elem , value) {
		if ( elem.hasClass('price-min') ) {
			priceSlider.noUiSlider.set([value, null]);
		} else if ( elem.hasClass('price-max')) {
			priceSlider.noUiSlider.set([null, value]);
		}
	}

	// Price Slider
	var priceSlider = document.getElementById('price-slider');
	if (priceSlider) {
		noUiSlider.create(priceSlider, {
			start: [priceInputMin.value,
			    priceInputMax.value ? priceInputMax.value : 9999
			],
			connect: true,
			step: 1,
			range: {
				'min': 1,
				'max': 9999
			}
		});

		priceSlider.noUiSlider.on('update', function( values, handle ) {
			var value = values[handle];
			handle ? priceInputMax.value = value : priceInputMin.value = value
		});
	}

	$('.js-product-on-click').on('click', function(){
        let href = $(this).find('.js-product-on-click-link').first().attr("href");
        if (href) {
            window.location = href;
        }
    });

    $('.js-product-action-submit').on('click', function() {
        let $button = $(this);
        let $form = $button.closest('.js-product-detail-action-form');
        let actionType = $button.data('action-type');

        if (actionType == 'buy-now') {
            $form.attr('action', $form.data('buy-now-action'));
            $form.attr('method', 'get');
            $form.find('.js-product-csrf-token').prop('disabled', true);
        } else {
            $form.attr('action', $form.data('cart-action'));
            $form.attr('method', 'post');
            $form.find('.js-product-csrf-token').prop('disabled', false);
        }
    });

    $('.js-on-click-remove-item').on('click', function(){
      let itemId = $(this).data('item-id');
      let token = $("meta[name='_csrf']").attr("content");
      let fieldName = $("meta[name='_csrf_parameter_name']").attr("content");
      let request = {
        url: "/shopping-cart/remove-item/" + itemId,
        method: "POST",
        data: {
            "_csrf": token
        }
      };
      $.ajax(request)
      .done(function() {
        window.location = "/shopping-cart?removed=true";
      })
      .fail(function() {
        alert( "error" );
      });
    });

//    $('.js-on-click-remove-item').on('click', function(){
//         let href = $(this).find('.js-product-on-click-link').first().attr("href");
//         if (href) {
//             window.location = href;
//         }
//    });

    function escapeHtml(value) {
        return $('<div>').text(value == null ? '' : value).html();
    }

    function creditCardLabel(creditCard) {
        let brand = creditCard.brand ? creditCard.brand : 'Credit card';
        return brand + ' ****-' + creditCard.lastNumbers + ' - ' + creditCard.expMonth + '/' + creditCard.expYear;
    }

    function renderCheckoutCreditCards(creditCards, selectedCreditCardId) {
        let html = '<option value="">Choose a credit card</option>';
        for (let creditCard of creditCards) {
            let selected = creditCard.id == selectedCreditCardId ? ' selected' : '';
            html += '<option value="' + escapeHtml(creditCard.id) + '"' + selected + '>' + escapeHtml(creditCardLabel(creditCard)) + '</option>';
        }
        return html;
    }

    function renderAccountCreditCards(creditCards) {
        let html = '';
        for (let creditCard of creditCards) {
            let brand = creditCard.brand || 'Credit card';
            html += '<tr class="js-credit-card-item">';
            html += '<th scope="row" class="border-0">';
            html += '<div class="p-2 shopping-cart-item-table-head">';
            html += '<div class="ml-3 d-inline-block align-middle">';
            html += '<h5 class="mb-0"><span class="text-dark d-inline-block align-middle">' + escapeHtml(brand) + ' ****-' + escapeHtml(creditCard.lastNumbers) + '</span></h5>';
            html += '</div>';
            html += '</div>';
            html += '</th>';
            html += '<td class="border-0 align-middle">' + escapeHtml(creditCard.expMonth) + '/' + escapeHtml(creditCard.expYear) + '</td>';
            html += '<td class="border-0 align-middle">';
            html += '<button class="text-dark js-on-click-remove-credit-card" data-credit-card-id="' + escapeHtml(creditCard.id) + '"><i class="fa fa-trash"></i></button>';
            html += '</td>';
            html += '</tr>';
        }
        return html;
    }

    function refreshCreditCardLists(selectedCreditCardId) {
        if ($('.js-credit-card-list').length == 0) {
            return;
        }

        $.ajax({
            url: '/my-account/credit-cards/list',
            type: 'GET',
            dataType: 'json'
        }).done(function(creditCards) {
            $('.js-credit-card-empty').toggle(creditCards.length == 0);
            $('.js-credit-card-list').each(function() {
                let mode = $(this).data('mode');
                if (mode == 'checkout') {
                    $(this).html(renderCheckoutCreditCards(creditCards, selectedCreditCardId));
                } else {
                    $(this).html(renderAccountCreditCards(creditCards));
                }
            });
        });
    }

    $(document).on('click', '.js-on-click-remove-credit-card', function() {
        let creditCardId = $(this).data('credit-card-id');
        $('#creditCardRemovalConfirm').data('credit-card-id', creditCardId);
        $('#creditCardRemovalError').addClass('hidden');
        $('#creditCardRemovalSuccess').addClass('hidden');
        $('#creditCardRemovalConfirmationModal').modal('show');
    });

    $(document).on('click', '#creditCardRemovalConfirm', function() {
        let creditCardId = $(this).data('credit-card-id');
        let token = $("meta[name='_csrf']").attr("content");

        $.ajax({
            url: "/my-account/credit-cards/remove/" + creditCardId,
            method: "POST",
            data: { "_csrf": token }
        }).done(function() {
            $('#creditCardRemovalConfirmationModal').modal('hide');
            refreshCreditCardLists();
            $('#creditCardRemovalSuccess').removeClass('hidden');
        }).fail(function() {
            $('#creditCardRemovalError').removeClass('hidden');
        });
    });

    function tokenizeCreditCard() {
        let $form = $('#creditCardRegistrationForm');
        let request = {
            number: $form.find('input[name="cardNumber"]').val(),
            holderName: $form.find('input[name="holderName"]').val(),
            holderDocument: $form.find('input[name="holderDocument"]').val(),
            expMonth: parseInt($form.find('input[name="expMonth"]').val(), 10),
            expYear: parseInt($form.find('input[name="expYear"]').val(), 10),
            cvv: $form.find('input[name="CVV"]').val()
        };

        return $.ajax({
            contentType: 'application/json',
            data: JSON.stringify(request),
            headers: {
                'Token': $form.data('public-token')
            },
            dataType: 'json',
            url: $form.data('token-url'),
            type: 'POST'
        });
    }

    function registerCreditCard(tokenizedCard) {
        let token = $("meta[name='_csrf']").attr("content");
        let headerName = $("meta[name='_csrf_header']").attr("content");
        let headers = {};
        headers[headerName] = token;

        return $.ajax({
            contentType: 'application/json',
            data: JSON.stringify({ tokenizedCard: tokenizedCard }),
            headers: headers,
            dataType: 'json',
            url: '/my-account/credit-cards',
            type: 'POST'
        });
    }

    $('#creditCardRegistrationSubmit').on('click', function() {
        let $button = $(this);
        $('#creditCardRegistrationError').addClass('hidden');
        $button.prop('disabled', true);

        tokenizeCreditCard()
            .then(function(data) {
                let tokenizedCard = typeof data === 'string' ? JSON.parse(data).tokenizedCard : data.tokenizedCard;
                return registerCreditCard(tokenizedCard);
            })
            .done(function(creditCard) {
                $('#creditCardRegistrationModal').modal('hide');
                $('#creditCardRegistrationForm')[0].reset();
                refreshCreditCardLists(creditCard.id);
            })
            .fail(function() {
                $('#creditCardRegistrationError').removeClass('hidden');
            })
            .always(function() {
                $button.prop('disabled', false);
            });
    });

    let checkoutShippingCostRequest = null;
    let checkoutShippingCostTimer = null;

    function shippingZipCode() {
        return $('input[name="shippingInfo.address.zipCode"]').val() || '';
    }

    function normalizedShippingZipCode() {
        return shippingZipCode().replace(/\D/g, '');
    }

    function shippingCostPreviewUrl() {
        return $('#checkoutForm').data('shipping-preview-url') || '/checkout/shipping-cost-preview';
    }

    function shippingCostPreviewPayload(zipCode) {
        let payload = { zipCode: zipCode };
        let productId = $('#checkoutForm input[name="productId"]').val();
        let quantity = $('#checkoutForm input[name="quantity"]').val();

        if (productId) {
            payload.productId = productId;
            payload.quantity = parseInt(quantity, 10) || 1;
        }

        return payload;
    }

    function resetCheckoutShippingCost(message) {
        let subtotal = $('.js-order-total').data('subtotal');
        $('.js-shipping-cost').text('-');
        $('.js-order-total').text(subtotal);
        $('.js-shipping-cost-status').text(message || '').toggleClass('hidden', !message);
    }

    function updateCheckoutShippingCost() {
        if ($('#checkoutForm').length == 0 || $('.js-shipping-cost').length == 0) {
            return;
        }

        let zipCode = normalizedShippingZipCode();

        if (zipCode.length != 5) {
            resetCheckoutShippingCost('Enter a valid zipcode.');
            return;
        }

        if (checkoutShippingCostRequest) {
            checkoutShippingCostRequest.abort();
        }

        let token = $("meta[name='_csrf']").attr("content");
        let headerName = $("meta[name='_csrf_header']").attr("content");
        let headers = {};
        headers[headerName] = token;

        $('.js-shipping-cost').text('Calculating...');
        $('.js-shipping-cost-status').addClass('hidden').text('');

        let currentRequest = $.ajax({
            contentType: 'application/json',
            data: JSON.stringify(shippingCostPreviewPayload(zipCode)),
            headers: headers,
            dataType: 'json',
            url: shippingCostPreviewUrl(),
            type: 'POST'
        });

        checkoutShippingCostRequest = currentRequest;

        currentRequest.done(function(data) {
            $('.js-shipping-cost').text(data.formattedCost || '-');
            $('.js-order-total').text(data.formattedTotalAmount || $('.js-order-total').data('subtotal'));
            $('.js-shipping-cost-status').addClass('hidden').text('');
        }).fail(function(_, status) {
            if (status == 'abort') {
                return;
            }
            resetCheckoutShippingCost('Shipping unavailable.');
        }).always(function() {
            if (checkoutShippingCostRequest == currentRequest) {
                checkoutShippingCostRequest = null;
            }
        });
    }

    function scheduleCheckoutShippingCostUpdate() {
        clearTimeout(checkoutShippingCostTimer);
        checkoutShippingCostTimer = setTimeout(updateCheckoutShippingCost, 300);
    }

    $('input[name="shippingInfo.address.zipCode"]').on('input change', scheduleCheckoutShippingCostUpdate);
    updateCheckoutShippingCost();

    $('#checkoutForm').submit(function(e) {
        //e.preventDefault();
        let paymentMethod = $('input[name="paymentMethod"]:checked').val();

        if (!paymentMethod) {
            return false;
        }

        if (paymentMethod != 'CREDIT_CARD') {
            return true;
        }

        if (!$('select[name="creditCardId"]').val()) {
            alert('Choose a credit card or add a new one.');
            return false;
        }

        return true;
    });

})(jQuery);

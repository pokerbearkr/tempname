window.addEventListener('DOMContentLoaded', async () => {
    const clientKey = 'test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm'; // Toss 클라이언트 키
    const customerKey = 'anonymous'; // 고객 식별값

    try {
        const paymentWidget = PaymentWidget(clientKey, customerKey);
        const widgets = paymentWidget.widgets({ customerKey });

        // 결제 금액 설정
        await widgets.setAmount({ value: 50000, currency: 'KRW' });

        // 결제 수단 및 약관 렌더링
        await Promise.all([
            widgets.renderPaymentMethods({
                selector: '#payment-method',
                variantKey: 'DEFAULT'
            }),
            widgets.renderAgreement({
                selector: '#agreement',
                variantKey: 'AGREEMENT'
            })
        ]);

        // 결제 버튼 이벤트 연결
        document.getElementById('payment-request-button').addEventListener('click', async () => {
            try {
                await widgets.requestPayment({
                    orderId: generateRandomOrderId(),
                    orderName: '토스 티셔츠 외 2건',
                    successUrl: window.location.origin + '/sandbox/success',
                    failUrl: window.location.origin + '/sandbox/fail',
                    customerEmail: 'test@example.com',
                    customerName: '홍길동',
                    customerMobilePhone: '01012345678'
                });
            } catch (error) {
                alert('결제 요청 실패: ' + error.message);
            }
        });

    } catch (e) {
        alert('Toss 위젯 로딩 실패: ' + e.message);
    }

    // 랜덤 주문 ID 생성
    function generateRandomOrderId() {
        return 'order-' + Math.random().toString(36).substring(2, 12);
    }
});

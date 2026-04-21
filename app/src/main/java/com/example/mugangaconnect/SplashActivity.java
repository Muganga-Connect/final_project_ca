@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // TEMPORARILY DISABLED TO FIND THE BUG
        // ImageView logo = findViewById(R.id.splash_logo);
        // Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.splash_fade_in);
        // logo.startAnimation(fadeIn);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
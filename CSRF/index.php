<?php

// use \Psr\Http\Message\ServerRequestInterface as Request;
// use \Psr\Http\Message\ResponseInterface as Response;
// use Slim\Factory\AppFactory;

// require __DIR__ . '/vendor/autoload.php';

// $app = AppFactory::create();

// // $config = ['settings' => ['displayErrorDetails' => true]]; 
// // $app = new Slim\App($config);

// $container = $app->getContainer();

// $container['view'] = function ($container) {
//     $view = new \Slim\Views\Twig(__DIR__ . '/resources/views', [
//         'cache' => false
//     ]);

//     $basePath = rtrim(str_ireplace('index.php', '', $container['request']->getUri()->getBasePath()), '/');
//     $view->addExtension(new \Slim\Views\TwigExtension($container['router'], $basePath));

//     return $view;
// };

// $app->get('/', function ($request, $response, $args){
//     return $this->view->render($response, 'home.twig');
// });

// $app->run();

use DI\Container;
use Slim\Csrf\Guard;
use Slim\Factory\AppFactory;

require __DIR__ . '/vendor/autoload.php';

// Start PHP session
session_start();

// Create Container
$container = new Container();
AppFactory::setContainer($container);

// Create App
$app = AppFactory::create();
$responseFactory = $app->getResponseFactory();

// Register Middleware On Container
$container->set('csrf', function () use ($responseFactory) {
    return new Guard($responseFactory);
});

$app->get('/api/route',function ($request, $response, $args) {
    $csrf = $this->get('csrf');
    $nameKey = $csrf->getTokenNameKey();
    $valueKey = $csrf->getTokenValueKey();
    $name = $request->getAttribute($nameKey);
    $value = $request->getAttribute($valueKey);

    $tokenArray = [
        $nameKey => $name,
        $valueKey => $value
    ];
    
    return $response->write(json_encode($tokenArray));
})->add('csrf');

$app->post('/api/myEndPoint',function ($request, $response, $args) {
    
})->add('csrf');

$app->run();
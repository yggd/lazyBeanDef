# `BeanDefinitionBuilder`と`BeanDefinitionRegistry`を使用して動的なBean定義を行う。

__注意:__ DIコンテナのライフサイクルがぐちゃぐちゃになるため、よいこは真似しないでね。

たいていの`ApplicationContext`が`BeanDefinitionRegistry`を実装していることを~~悪用~~利用しています。
ここからBean定義の登録を行うと、以下のようなことがプログラム上で行えるようになります。

1.  prototypeスコープのBeanをコンテナから取得する。
2.  それをdependency-propertyとして新たなsingletonをコンテナに登録する。

詳細は `LazyBeanDefApplication.kt`を参照のこと。
